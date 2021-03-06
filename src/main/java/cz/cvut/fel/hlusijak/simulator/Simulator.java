package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.StateColoringMethod;
import cz.cvut.fel.hlusijak.util.FutureUtil;
import cz.cvut.fel.hlusijak.util.Wrapper;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The core of the entire ruleseeker project.
 * This class is used to iterate a cellular automaton simulation. It utilizes all available processor cores by running
 * the simulations in parallel.
 */
public class Simulator {
    private Grid grid;
    private RuleSet ruleSet;
    private StateColoringMethod stateColoringMethod;
    private int iteration;
    private int cellsPerTask;

    /**
     * @param grid The grid to iterate.
     * @param ruleSet The rule set to use to determine the next tile states.
     * @param stateColoringMethod The coloring method to use when rendering this simulation.
     * @param cellsPerTask The number of cells to designate for each piece of work.
     *                     These pieces are then processed by a threadpool in parallel.
     */
    public Simulator(Grid grid, RuleSet ruleSet, StateColoringMethod stateColoringMethod, int cellsPerTask) {
        this.grid = grid;
        this.ruleSet = ruleSet;
        this.stateColoringMethod = stateColoringMethod;
        this.iteration = 0;
        this.cellsPerTask = cellsPerTask;
    }

    /**
     * @param grid The grid to iterate.
     * @param ruleSet The rule set to use to determine the next tile states.
     * @param stateColoringMethod The coloring method to use when rendering this simulation.
     */
    public Simulator(Grid grid, RuleSet ruleSet, StateColoringMethod stateColoringMethod) {
        this(grid, ruleSet, stateColoringMethod, 32);
    }

    private Simulator() {
        // Required by Kryo
    }

    private CompletableFuture<Integer> recursiveFuture(Function<IterationResult, CompletableFuture<Boolean>> onIterationComplete) {
        return nextIterationAsync().exceptionally(exception -> {
            exception.printStackTrace();
            return null;
        }).thenCompose(iterationResult -> {
            if (iterationResult != null) {
                return onIterationComplete.apply(iterationResult).thenCompose(cont -> {
                    if (cont) {
                        return recursiveFuture(onIterationComplete);
                    } else {
                        return CompletableFuture.completedFuture(iteration);
                    }
                });
            } else {
                return CompletableFuture.completedFuture(iteration - 1);
            }
        });
    }

    /**
     * @param onIterationComplete A function to be called whenever an iteration has been completed.
     * @return The number of iterations that have been simulated.
     */
    public CompletableFuture<Integer> runAsync(Function<IterationResult, CompletableFuture<Boolean>> onIterationComplete) {
        return FutureUtil.futureTaskBackground(v -> recursiveFuture(onIterationComplete));
    }

    /**
     * Asynchronously executes an iteration.
     *
     * @return A conveniently informational {@link IterationResult}.
     */
    public synchronized CompletableFuture<IterationResult> nextIterationAsync() {
        /*
        Create final immutable instances of the previous iteration
         */
        final Instant computationStart = Instant.now();
        final Grid previousGrid = grid.clone();
        final RuleSet currentRuleSet = ruleSet;
        final int gridSize = previousGrid.getGeometry().getSize();
        final boolean additionalTask = Math.floorMod(gridSize, cellsPerTask) != 0;
        final int taskCount = gridSize / cellsPerTask + (additionalTask ? 1 : 0);
        // Wow, Java, nice generics.
        //noinspection unchecked
        CompletableFuture<byte[]>[] tasks = new CompletableFuture[taskCount];

        /*
        Divide up the work to several tasks.
         */
        for (int taskIndex = 0; taskIndex < taskCount; taskIndex += 1) {
            final int currentTaskIndex = taskIndex;
            final int currentCellsPerTask;

            if (additionalTask && taskIndex >= taskCount - 1) {
                currentCellsPerTask = gridSize - taskIndex * cellsPerTask;
            } else {
                currentCellsPerTask = cellsPerTask;
            }

            CompletableFuture<byte[]> taskFuture = FutureUtil.futureTaskBackground(() -> {
                final int cellIndexOffset = currentTaskIndex * cellsPerTask;
                final byte[] newStates = new byte[currentCellsPerTask];

                for (int relativeTileIndex = 0; relativeTileIndex < currentCellsPerTask; relativeTileIndex += 1) {
                    int absoluteTileIndex = relativeTileIndex + cellIndexOffset;
                    newStates[relativeTileIndex] = currentRuleSet.getNextTileState(previousGrid, absoluteTileIndex);
                }

                return newStates;
            });

            tasks[taskIndex] = taskFuture;
        }

        /*
        Combine the results to a single Grid.
         */
        Wrapper<Grid> nextGrid = new Wrapper<>(previousGrid.clone());

        Supplier<Integer> combine = () -> {
            int offset = 0;

            for (CompletableFuture<byte[]> task : tasks) {
                byte[] newStates;

                try {
                    newStates = task.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Unreachable due to the usage of CompletableFuture#allOf
                    throw new RuntimeException(e);
                }

                nextGrid.value.setTileStates(offset, newStates);

                offset += newStates.length;
            }

            this.grid = nextGrid.value.clone();
            this.iteration += 1;

            return this.iteration;
        };

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(tasks);

        return allTasks.thenApplyAsync(v -> {
            int iterationsCompleted;

            synchronized (this) {
                iterationsCompleted = combine.get();
            }

            Instant computationFinish = Instant.now();

            return new IterationResult(iterationsCompleted, computationStart, computationFinish, previousGrid, nextGrid.value);
        }, FutureUtil.getBackgroundExecutor());
    }

    /**
     * Executes an iteration synchronously and returns the {@link IterationResult}.
     */
    public synchronized IterationResult nextIteration() {
        try {
            return nextIterationAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Grid getGrid() {
        return grid;
    }

    public synchronized void setGrid(Grid grid) {
        this.grid = grid;
    }

    public synchronized RuleSet getRuleSet() {
        return ruleSet;
    }

    public synchronized void setRuleSet(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    public synchronized StateColoringMethod getStateColoringMethod() {
        return stateColoringMethod;
    }

    public synchronized void setStateColoringMethod(StateColoringMethod stateColoringMethod) {
        this.stateColoringMethod = stateColoringMethod;
    }

    public synchronized int getIterationsExecuted() {
        return iteration;
    }

    public synchronized int getCellsPerTask() {
        return cellsPerTask;
    }

    public synchronized void setCellsPerTask(int cellsPerTask) {
        this.cellsPerTask = cellsPerTask;
    }

    @Override
    public Simulator clone() {
        return new Simulator(grid.clone(), ruleSet.clone(), stateColoringMethod != null ? stateColoringMethod.copy() : null, cellsPerTask);
    }
}

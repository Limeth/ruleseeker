package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.util.FutureUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

public class Simulator {
    private Grid grid;
    private RuleSet ruleSet;
    private int iteration;
    private int cellsPerTask;

    public Simulator(Grid grid, RuleSet ruleSet, int cellsPerTask) {
        this.grid = grid;
        this.ruleSet = ruleSet;
        this.iteration = 0;
        this.cellsPerTask = cellsPerTask;
    }

    private CompletableFuture<Integer> recursiveFuture(Function<Integer, CompletableFuture<Boolean>> onIterationComplete) {
        return nextIterationAsync().thenCompose(onIterationComplete).thenCompose(cont -> {
            if (cont) {
                return recursiveFuture(onIterationComplete);
            } else {
                return CompletableFuture.completedFuture(iteration);
            }
        });
    }

    public CompletableFuture<Integer> runAsync(Function<Integer, CompletableFuture<Boolean>> onIterationComplete) {
        return FutureUtil.futureTaskBackground(v -> {
            synchronized (this) {
                return recursiveFuture(onIterationComplete);
            }
        });
    }

    /**
     * Executes an iteration of the simulation in a paralellized way.
     * @return The number of iterations executed including the requested one.
     */
    private CompletableFuture<Integer> nextIterationAsync() {
        final Grid currentGrid = grid;
        final RuleSet currentRuleSet = ruleSet;
        final int gridSize = currentGrid.getGeometry().getSize();
        final boolean additionalTask = Math.floorMod(gridSize, cellsPerTask) != 0;
        final int taskCount = gridSize / cellsPerTask + (additionalTask ? 1 : 0);
        // Wow, Java, nice generics.
        //noinspection unchecked
        CompletableFuture<int[]>[] tasks = new CompletableFuture[taskCount];

        for (int taskIndex = 0; taskIndex < taskCount; taskIndex += 1) {
            final int currentTaskIndex = taskIndex;
            final int currentCellsPerTask;

            if (additionalTask && taskIndex >= taskCount - 1) {
                currentCellsPerTask = gridSize - taskIndex * cellsPerTask;
            } else {
                currentCellsPerTask = cellsPerTask;
            }

            CompletableFuture<int[]> taskFuture = FutureUtil.futureTaskBackground(() -> {
                final int cellIndexOffset = currentTaskIndex * cellsPerTask;
                final int[] newStates = new int[currentCellsPerTask];

                for (int relativeTileIndex = 0; relativeTileIndex < currentCellsPerTask; relativeTileIndex += 1) {
                    int absoluteTileIndex = relativeTileIndex + cellIndexOffset;
                    newStates[relativeTileIndex] = currentRuleSet.getNextTileState(currentGrid, absoluteTileIndex);
                }

                return newStates;
            });

            tasks[taskIndex] = taskFuture;
        }

        Supplier<Integer> combine = () -> {
            int offset = 0;

            for (CompletableFuture<int[]> task : tasks) {
                int[] newStates;

                try {
                    newStates = task.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Unreachable due to the usage of CompletableFuture#allOf
                    throw new RuntimeException(e);
                }

                grid.setTileStates(offset, newStates);

                offset += newStates.length;
            }

            this.iteration += 1;

            return this.iteration;
        };

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(tasks);

        return allTasks.thenApplyAsync(v -> {
            synchronized (this) {
                return combine.get();
            }
        }, FutureUtil.getBackgroundExecutor());
    }

    /**
     * Executes an iteration of the simulation in a paralellized way.
     * @return The number of iterations executed including the requested one.
     */
    public synchronized CompletableFuture<Integer> nextIteration() {
        return nextIterationAsync();
    }

    public synchronized Grid getGrid() {
        return grid.clone();
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

    public synchronized int getIterationsExecuted() {
        return iteration;
    }

    public synchronized int getCellsPerTask() {
        return cellsPerTask;
    }

    public synchronized void setCellsPerTask(int cellsPerTask) {
        this.cellsPerTask = cellsPerTask;
    }
}

package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Simulator {
    private Grid grid;
    private RuleSet ruleSet;
    private int iteration;
    private int cellsPerTask;
    private ExecutorService simulationExecutorService;

    public Simulator(Grid grid, RuleSet ruleSet, int cellsPerTask) {
        this.grid = grid;
        this.ruleSet = ruleSet;
        this.iteration = 0;
        this.cellsPerTask = cellsPerTask;
        this.simulationExecutorService = Executors.newWorkStealingPool();
    }

    /**
     * Executes an iteration of the simulation in a paralellized way.
     * @return The number of iterations executed including the requested one.
     */
    public synchronized Future<Integer> nextIteration() {
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

            CompletableFuture<int[]> taskFuture = CompletableFuture.supplyAsync(() -> {
                final int cellIndexOffset = currentTaskIndex * cellsPerTask;
                final int[] newStates = new int[currentCellsPerTask];

                for (int relativeTileIndex = 0; relativeTileIndex < currentCellsPerTask; relativeTileIndex += 1) {
                    int absoluteTileIndex = relativeTileIndex + cellIndexOffset;
                    newStates[relativeTileIndex] = currentRuleSet.getNextTileState(currentGrid, absoluteTileIndex);
                }

                return newStates;
            }, simulationExecutorService);

            tasks[taskIndex] = taskFuture;
        }

        return CompletableFuture.allOf(tasks).thenApply(v -> {
            synchronized (this) {
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
            }
        });
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

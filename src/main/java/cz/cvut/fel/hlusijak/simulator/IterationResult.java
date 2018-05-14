package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;

import java.time.Instant;

/**
 * Holds the result of an iteration executed by a {@link Simulator}.
 */
public class IterationResult {
    private final int iterationsCompleted;
    private final Instant computationStart;
    private final Instant computationFinish;
    private final Grid previousGrid;
    private final Grid nextGrid;

    IterationResult(int iterationsCompleted, Instant computationStart, Instant computationFinish, Grid previousGrid, Grid nextGrid) {
        this.iterationsCompleted = iterationsCompleted;
        this.computationStart = computationStart;
        this.computationFinish = computationFinish;
        this.previousGrid = previousGrid;
        this.nextGrid = nextGrid;
    }

    /**
     * @return The number of iterations executed in total from the beginning of the simulation.
     */
    public int getIterationsCompleted() {
        return iterationsCompleted;
    }

    /**
     * @return The {@link Instant} the computation of the next grid has begun.
     */
    public Instant getComputationStart() {
        return computationStart;
    }

    /**
     * @return The {@link Instant} the computation of the next grid has finished.
     */
    public Instant getComputationFinish() {
        return computationFinish;
    }

    /**
     * @return The previous grid that was used to compute the next grid
     */
    public Grid getPreviousGrid() {
        return previousGrid;
    }

    /**
     * @return The next grid that was just computed
     */
    public Grid getNextGrid() {
        return nextGrid;
    }
}

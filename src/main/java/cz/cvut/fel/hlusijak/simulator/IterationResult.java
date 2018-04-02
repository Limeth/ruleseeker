package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;

import java.time.Instant;

public class IterationResult {
    private final int iterationsCompleted;
    private final Instant computationStart;
    private final Instant computationFinish;
    private final Grid previousGrid;
    private final Grid nextGrid;

    public IterationResult(int iterationsCompleted, Instant computationStart, Instant computationFinish, Grid previousGrid, Grid nextGrid) {
        this.iterationsCompleted = iterationsCompleted;
        this.computationStart = computationStart;
        this.computationFinish = computationFinish;
        this.previousGrid = previousGrid;
        this.nextGrid = nextGrid;
    }

    public int getIterationsCompleted() {
        return iterationsCompleted;
    }

    public Instant getComputationStart() {
        return computationStart;
    }

    public Instant getComputationFinish() {
        return computationFinish;
    }

    public Grid getPreviousGrid() {
        return previousGrid;
    }

    public Grid getNextGrid() {
        return nextGrid;
    }
}

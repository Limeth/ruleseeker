package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;

/**
 * Calculates the new state of a given tile for a particular Grid.
 * No writes to shared memory must occur in the implementation.
 */
@FunctionalInterface
public interface RuleSet {
    int getNextTileState(Grid grid, int tileIndex);
}

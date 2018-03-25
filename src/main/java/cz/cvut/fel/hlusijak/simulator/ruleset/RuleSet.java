package cz.cvut.fel.hlusijak.simulator.ruleset;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;

public interface RuleSet {
    /**
     * @return The number of states.
     */
    int getNumberOfStates();

    /**
     * Calculates the new state of a given tile for a particular GridGeometry.
     * No writes to shared memory must occur in the implementation.
     */
    int getNextTileState(Grid grid, int tileIndex);
}

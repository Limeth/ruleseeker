package cz.cvut.fel.hlusijak.simulator.grid;

import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;

import java.util.Random;

/**
 * Stores states of cells with properties defined by {@link GridGeometry}.
 */
public class Grid {
    private final GridGeometry geometry;
    private final int[] states;

    public Grid(GridGeometry geometry) {
        this.geometry = geometry;
        this.states = new int[geometry.getSize()];
    }

    public GridGeometry getGeometry() {
        return geometry;
    }

    /**
     * @param tileIndex The index of the tile to get the state index of.
     * @return The state index of the specified tile.
     */
    public int getTileState(int tileIndex) {
        return states[tileIndex];
    }

    /**
     * @param tileIndex The index of the tile to change the state index of.
     * @param tileState The new state index of the tile.
     * @return The previous state index of the specified tile.
     */
    public int setTileState(int tileIndex, int tileState) {
        int tmp = states[tileIndex];
        states[tileIndex] = tileState;

        return tmp;
    }

    public void randomizeTileStates(Random rng, RuleSet ruleSet) {
        int numberOfStates = ruleSet.getNumberOfStates();

        for (int i = 0; i < states.length; i++) {
            states[i] = rng.nextInt(numberOfStates);
        }
    }

    /**
     * @param tileIndexOffset The index of the tile to start changing the state
     *                        indices from.
     * @param tileStates The new state indices.
     */
    public void setTileStates(int tileIndexOffset, int[] tileStates) {
        System.arraycopy(tileStates, 0, states, tileIndexOffset, tileStates.length);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Grid clone() {
        Grid cloned = new Grid(geometry);

        System.arraycopy(states, 0, cloned.states, 0, states.length);

        return cloned;
    }
}

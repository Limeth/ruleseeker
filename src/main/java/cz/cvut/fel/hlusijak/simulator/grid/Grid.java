package cz.cvut.fel.hlusijak.simulator.grid;

import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.util.VariedUtil;

import java.util.Arrays;
import java.util.Random;

/**
 * Stores states of cells with properties defined by {@link GridGeometry}.
 */
public class Grid {
    private final GridGeometry geometry;
    private final byte[] states;

    public Grid(GridGeometry geometry) {
        this.geometry = geometry;
        this.states = new byte[geometry.getSize()];
    }

    private Grid() {
        // Required by Kryo
        this.geometry = null;
        this.states = null;
    }

    public GridGeometry getGeometry() {
        return geometry;
    }

    /**
     * @param tileIndex The index of the tile to get the state index of.
     * @return The state index of the specified tile.
     */
    public byte getTileState(int tileIndex) {
        return states[tileIndex];
    }

    /**
     * @param tileIndex The index of the tile to change the state index of.
     * @param tileState The new state index of the tile.
     * @return The previous state index of the specified tile.
     */
    public int setTileState(int tileIndex, byte tileState) {
        int tmp = states[tileIndex];
        states[tileIndex] = tileState;

        return tmp;
    }

    public void randomizeTileStates(Random rng, RuleSet ruleSet) {
        int numberOfStates = ruleSet.getType().getNumberOfStates();

        VariedUtil.randomBoundedByteArray(rng, states, numberOfStates);
    }

    /**
     * @param tileIndexOffset The index of the tile to start changing the state
     *                        indices from.
     * @param tileStates The new state indices.
     */
    public void setTileStates(int tileIndexOffset, byte[] tileStates) {
        System.arraycopy(tileStates, 0, states, tileIndexOffset, tileStates.length);
    }

    public void fillTileStates(byte state) {
        Arrays.fill(this.states, state);
    }

    public byte[] getTileStateChunk(int offset, int maxLength) {
        int length = Math.max(0, Math.min(states.length - offset, maxLength));
        byte[] result = new byte[length];

        if (length <= 0) {
            return result;
        }

        System.arraycopy(states, offset, result, 0, length);

        return result;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Grid clone() {
        Grid cloned = new Grid(geometry);

        System.arraycopy(states, 0, cloned.states, 0, states.length);

        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Grid)) {
            return false;
        }

        Grid other = (Grid) o;
        return geometry.equals(other.geometry) && Arrays.equals(states, other.states);
    }
}

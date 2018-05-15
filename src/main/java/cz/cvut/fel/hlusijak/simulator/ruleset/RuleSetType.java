package cz.cvut.fel.hlusijak.simulator.ruleset;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.util.VariedUtil;

import java.util.stream.Stream;

/**
 * Determines the total size of the rule set given a {@link GridGeometry} and
 * the number of total states; provides a way to calculate the rule index from
 * a given tile index.
 */
public interface RuleSetType {
    /**
     * @return The number of states.
     */
    byte getNumberOfStates();

    /**
     * @return The grid geometry this rule set operates on.
     */
    GridGeometry getGridGeometry();

    /**
     * @return The total amount of rules required.
     */
    int getRuleSetSize();

    /**
     * Calculates the index of the outcome for the specified {@param tileIndex}.
     * No writes to shared memory must occur in the implementation.
     */
    int getRuleIndex(Grid grid, int tileIndex);

    /**
     * @return A stream over all possible cell states.
     */
    default Stream<Byte> stateStream() {
        return VariedUtil.byteStreamRange((byte) 0, getNumberOfStates());
    }

    /**
     * @return A deep copy of this {@link RuleSetType} instance.
     */
    RuleSetType copy();
}

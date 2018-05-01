package cz.cvut.fel.hlusijak.simulator.ruleset;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.util.VariedUtil;

import java.util.Random;
import java.util.stream.Stream;

public interface RuleSet {
    /**
     * @return The number of states.
     */
    byte getNumberOfStates();

    /**
     * Calculates the new state of a given tile for a particular GridGeometry.
     * No writes to shared memory must occur in the implementation.
     */
    byte getNextTileState(Grid grid, int tileIndex);

    /**
     * @return An array of next states where the index is a representation of
     *         the tiles' surroundings.
     */
    byte[] getRules();

    /**
     * @param rules An array of next states where the index is a representation of
     *              the tiles' surroundings.
     */
    void setRules(byte[] rules);

    /**
     * Assigns a random outcome state to each rule.
     *
     * @param rng The desired random number generator to generate the states with, in succession.
     */
    default void randomizeRules(Random rng) {
        int numberOfStates = getNumberOfStates();
        byte[] rules = getRules();

        VariedUtil.randomBoundedByteArray(rng, rules, numberOfStates);

        setRules(rules);
    }

    /**
     * @return A stream over all possible cell states.
     */
    default Stream<Byte> stateStream() {
        return VariedUtil.byteStreamRange((byte) 0, getNumberOfStates());
    }

    RuleSet copy();
}

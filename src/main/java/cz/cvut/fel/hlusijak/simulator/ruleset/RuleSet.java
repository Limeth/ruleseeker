package cz.cvut.fel.hlusijak.simulator.ruleset;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

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

    /**
     * @return An array of next states where the index is a representation of
     *         the tiles' surroundings.
     */
    int[] getRules();

    /**
     * @param rules An array of next states where the index is a representation of
     *              the tiles' surroundings.
     */
    void setRules(int[] rules);

    /**
     * Assigns a random outcome state to each rule.
     *
     * @param rng The desired random number generator to generate the states with, in succession.
     */
    default void randomizeRules(Random rng) {
        int numberOfStates = getNumberOfStates();
        int[] rules = getRules();

        for (int i = 0; i < rules.length; i++) {
            rules[i] = rng.nextInt(numberOfStates);
        }

        setRules(rules);
    }

    /**
     * @return A stream over all possible cell states.
     */
    default IntStream stateStream() {
        return IntStream.range(0, getNumberOfStates());
    }

    /**
     * This method is very expensive. Make sure to cache the result.
     *
     * @return Creates a unique hue offset in the <0; 1) range for the current rules.
     */
    default double getHueOffset() {
        return new Random(Arrays.hashCode(getRules())).nextDouble();
    }

    RuleSet copy();
}

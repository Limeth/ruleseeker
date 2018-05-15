package cz.cvut.fel.hlusijak.simulator.ruleset;

import com.google.common.base.Preconditions;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.util.VariedUtil;

import java.util.Arrays;
import java.util.Random;

/**
 * This class contains the rules used to determine the next state of each grid
 * cell in the following iteration.
 */
public class RuleSet {
    private final RuleSetType type;
    private final byte[] rules;

    public RuleSet(RuleSetType type, byte[] rules) {
        Preconditions.checkNotNull(type, "The rule set type must not be null.");

        this.type = type;
        int size = type.getRuleSetSize();

        if (rules == null) {
            rules = new byte[size];
        } else {
            Preconditions.checkArgument(rules.length == size,
                    String.format("The rules array must be of length %d, but is of length %d", size, rules.length));
        }

        this.rules = Arrays.copyOf(rules, rules.length);
    }

    public RuleSet(RuleSetType type) {
        this(type, null);
    }

    private RuleSet() {
        // Required by Kryo
        this.type = null;
        this.rules = null;
    }

    /**
     * Calculates the new state of a given tile for a particular GridGeometry.
     * No writes to shared memory must occur in the implementation.
     */
    public byte getNextTileState(Grid grid, int tileIndex) {
        return rules[type.getRuleIndex(grid, tileIndex)];
    }

    /**
     * Assigns a random outcome state to each rule.
     *
     * @param rng The desired random number generator to generate the states with, in succession.
     */
    public void randomizeRules(Random rng) {
        int states = type.getNumberOfStates();
        byte[] rules = getRules();

        VariedUtil.randomBoundedByteArray(rng, rules, states);

        setRules(rules);
    }

    /**
     * @return The type of this rule set.
     */
    public RuleSetType getType() {
        return type;
    }

    /**
     * @return A specific rule at the given {@param index}.
     */
    public byte getRule(int index) {
        return rules[index];
    }

    /**
     * @return An array of next states where the index is a representation of
     *         the tiles' surroundings.
     */
    public byte[] getRules() {
        return Arrays.copyOf(rules, rules.length);
    }

    /**
     * @return A slice of the rule set array at the given {@param offset} with the maximum length of {@param maxLength}.
     */
    public byte[] getRuleSetChunk(int offset, int maxLength) {
        return VariedUtil.byteSlice(rules, offset, maxLength);
    }

    /**
     * @param rules An array of next states where the index is a representation of
     *              the tiles' surroundings.
     */
    public void setRules(byte[] rules) {
        Preconditions.checkArgument(rules.length == this.rules.length,
                String.format("The rules array must be of length %d, but is of length %d", this.rules.length, rules.length));
        System.arraycopy(rules, 0, this.rules, 0, rules.length);
    }

    public void setRule(int index, byte outcome) {
        rules[index] = outcome;
    }

    @Override
    public RuleSet clone() {
        return new RuleSet(type.copy(), rules);
    }

    @Override
    public String toString() {
        return "RuleSet{" +
                "type=" + type +
                ", rules=" + Arrays.toString(rules) +
                '}';
    }
}

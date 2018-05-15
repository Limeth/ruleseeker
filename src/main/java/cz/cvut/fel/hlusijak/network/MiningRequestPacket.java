package cz.cvut.fel.hlusijak.network;

import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSetType;

/**
 * Direction: Master -> Slave
 *
 * A request to begin mining, holding the necessary initial & success conditions.
 */
public class MiningRequestPacket implements Packet {
    private final RuleSetType ruleSetType;
    private final int minIterations;
    private final int maxIterations;

    /**
     * @param ruleSetType The rule set type to mine.
     * @param minIterations The minimum number of iterations until the
     *                      simulation dies out to be accepted as satisfactory
     * @param maxIterations The maximum number of iterations until the
     *                      simulation dies out to be accepted as satisfactory
     */
    public MiningRequestPacket(RuleSetType ruleSetType, int minIterations, int maxIterations) {
        this.ruleSetType = ruleSetType;
        this.minIterations = minIterations;
        this.maxIterations = maxIterations;
    }

    public MiningRequestPacket(Simulator seed, int minIterations, int maxIterations) {
        this(seed.getRuleSet().getType(), minIterations, maxIterations);
    }

    // Kryonet requires a default constructor for registered classes
    private MiningRequestPacket() {
        this((RuleSetType) null, 0, 0);
    }

    /**
     * @return The rule set type to mine.
     */
    public RuleSetType getRuleSetType() {
        return ruleSetType;
    }

    /**
     * @return The minimum number of iterations until the simulation dies out to
     *         be accepted as satisfactory
     */
    public int getMinIterations() {
        return minIterations;
    }

    /**
     * @return The maximum number of iterations until the simulation dies out to
     *         be accepted as satisfactory
     */
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public String toString() {
        return "MiningRequestPacket{" +
                ", ruleSetType=" + ruleSetType +
                ", minIterations=" + minIterations +
                ", maxIterations=" + maxIterations +
                '}';
    }
}

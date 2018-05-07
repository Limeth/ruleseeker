package cz.cvut.fel.hlusijak.network;

import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSetType;

public class MiningRequestPacket implements Packet {
    private final RuleSetType ruleSetType;
    private final int minIterations;
    private final int maxIterations;

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

    public RuleSetType getRuleSetType() {
        return ruleSetType;
    }

    public int getMinIterations() {
        return minIterations;
    }

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

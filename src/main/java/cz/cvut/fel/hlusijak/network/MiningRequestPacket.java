package cz.cvut.fel.hlusijak.network;

import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSetType;

public class MiningRequestPacket implements Packet {
    private final RuleSetType ruleSetType;
    private final GridGeometry gridGeometry;
    private final int minIterations;
    private final int maxIterations;

    public MiningRequestPacket(RuleSetType ruleSetType, GridGeometry gridGeometry, int minIterations, int maxIterations) {
        this.ruleSetType = ruleSetType;
        this.gridGeometry = gridGeometry;
        this.minIterations = minIterations;
        this.maxIterations = maxIterations;
    }

    public MiningRequestPacket(Simulator seed, int minIterations, int maxIterations) {
        this(seed.getRuleSet().getType(), seed.getGrid().getGeometry(), minIterations, maxIterations);
    }

    // Kryonet requires a default constructor for registered classes
    private MiningRequestPacket() {
        this(null, null, 0, 0);
    }

    public RuleSetType getRuleSetType() {
        return ruleSetType;
    }

    public GridGeometry getGridGeometry() {
        return gridGeometry;
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
                ", gridGeometry=" + gridGeometry +
                ", minIterations=" + minIterations +
                ", maxIterations=" + maxIterations +
                '}';
    }
}

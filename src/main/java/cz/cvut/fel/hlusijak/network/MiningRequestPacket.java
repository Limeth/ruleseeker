package cz.cvut.fel.hlusijak.network;

import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;

public class MiningRequestPacket implements Packet {
    private final RuleSet originalRuleSet;
    private final GridGeometry gridGeometry;
    private final int minIterations;
    private final int maxIterations;

    public MiningRequestPacket(RuleSet originalRuleSet, GridGeometry gridGeometry, int minIterations, int maxIterations) {
        this.originalRuleSet = originalRuleSet;
        this.gridGeometry = gridGeometry;
        this.minIterations = minIterations;
        this.maxIterations = maxIterations;
    }

    public MiningRequestPacket(Simulator seed, int minIterations, int maxIterations) {
        this(seed.getRuleSet(), seed.getGrid().getGeometry(), minIterations, maxIterations);
    }

    // Kryonet requires a default constructor for registered classes
    private MiningRequestPacket() {
        this(null, null, 0, 0);
    }

    public RuleSet getOriginalRuleSet() {
        return originalRuleSet;
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
                ", originalRuleSet=" + originalRuleSet +
                ", gridGeometry=" + gridGeometry +
                ", minIterations=" + minIterations +
                ", maxIterations=" + maxIterations +
                '}';
    }
}

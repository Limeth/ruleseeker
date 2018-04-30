package cz.cvut.fel.hlusijak.network;

import java.util.Optional;

import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;

public class MiningResultPacket implements Packet {
    private final RuleSet ruleSet;

    public MiningResultPacket(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    // Kryonet requires a default constructor for registered classes
    private MiningResultPacket() {
        this(null);
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    @Override
    public String toString() {
        return "MiningResultPacket{" +
                "ruleSet='" + ruleSet + '\'' +
                '}';
    }
}

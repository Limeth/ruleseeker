package cz.cvut.fel.hlusijak.network;

import java.util.Optional;

import cz.cvut.fel.hlusijak.simulator.Simulator;

public class MiningRequestPacket implements Packet {
    private final Simulator seed;
    private final int minIterations;
    private final int maxIterations;

    public MiningRequestPacket(Simulator seed, int minIterations, int maxIterations) {
        this.seed = seed;
        this.minIterations = minIterations;
        this.maxIterations = maxIterations;
    }

    // Kryonet requires a default constructor for registered classes
    private MiningRequestPacket() {
        this(null, 0, 0);
    }

    public Simulator getSeed() {
        return seed;
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
                "seed='" + seed +
                "', minIterations=" + minIterations +
                ", maxIterations=" + maxIterations +
                '}';
    }
}

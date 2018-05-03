package cz.cvut.fel.hlusijak.network;

import java.util.Arrays;

public class MiningResultPacket implements Packet {
    private final byte[] ruleSetChunk;
    private final int ruleSetChunkOffset;

    public MiningResultPacket(byte[] ruleSetChunk, int ruleSetChunkOffset) {
        this.ruleSetChunk = ruleSetChunk;
        this.ruleSetChunkOffset = ruleSetChunkOffset;
    }

    // Kryonet requires a default constructor for registered classes
    private MiningResultPacket() {
        this(null, 0);
    }

    public byte[] getRuleSetChunk() {
        return ruleSetChunk;
    }

    public int getRuleSetChunkOffset() {
        return ruleSetChunkOffset;
    }

    @Override
    public String toString() {
        return "MiningResultPacket{" +
                "ruleSetChunk='" + Arrays.toString(ruleSetChunk) + '\'' +
                ", ruleSetChunkOffset='" + ruleSetChunkOffset + '\'' +
                '}';
    }
}

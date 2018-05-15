package cz.cvut.fel.hlusijak.network;

import cz.cvut.fel.hlusijak.Master;

import java.util.Arrays;

/**
 * Direction: Slave -> Master
 *
 * Used to transfer a satisfactory rule set to the {@link Master}.
 * This packet holds a part of that rule set, which is fragmented across
 * multiple {@link MiningResultPacket}s.
 */
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

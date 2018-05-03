package cz.cvut.fel.hlusijak.network;

import java.util.Arrays;

public class SeedGridChunkPacket implements Packet {
    private final byte[] gridChunk;
    private final int gridChunkOffset;

    public SeedGridChunkPacket(byte[] gridChunk, int gridChunkOffset) {
        this.gridChunk = gridChunk;
        this.gridChunkOffset = gridChunkOffset;
    }

    // Kryonet requires a default constructor for registered classes
    private SeedGridChunkPacket() {
        this(null, 0);
    }

    public byte[] getGridChunk() {
        return gridChunk;
    }

    public int getGridChunkOffset() {
        return gridChunkOffset;
    }

    @Override
    public String toString() {
        return "SeedGridChunkPacket{" +
                "gridChunk=" + Arrays.toString(gridChunk) +
                ", gridChunkOffset=" + gridChunkOffset +
                '}';
    }
}

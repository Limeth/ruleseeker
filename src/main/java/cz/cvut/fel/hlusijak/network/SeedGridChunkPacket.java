package cz.cvut.fel.hlusijak.network;

import cz.cvut.fel.hlusijak.Slave;

import java.util.Arrays;

/**
 * Direction: Master -> Slave
 *
 * Used to transfer the initial conditions of a simulation to the {@link Slave}.
 * This packet holds a part of the initial grid, which is fragmented across
 * multiple {@link SeedGridChunkPacket}s.
 */
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

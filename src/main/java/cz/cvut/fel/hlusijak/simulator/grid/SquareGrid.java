package cz.cvut.fel.hlusijak.simulator.grid;

import com.google.common.base.Preconditions;

/**
 * A grid of squares.
 */
public class SquareGrid implements Grid {
    private final int width, height, size;
    private final int[] states;

    public SquareGrid(int width, int height) {
        Preconditions.checkArgument(width > 0, "The width must be positive.");
        Preconditions.checkArgument(height > 0, "The height must be positive.");

        this.width = width;
        this.height = height;
        this.size = width * height;
        this.states = new int[size];
    }

    private SquareGrid(int width, int height, int size) {
        this.width = width;
        this.height = height;
        this.size = size;
        this.states = new int[size];
    }

    public int getTileIndex(int x, int y) {
        return x + y * width;
    }

    @Override
    public int getNeighbourCount() {
        return 4;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getTileState(int tileIndex) {
        return states[tileIndex];
    }

    @Override
    public int setTileState(int tileIndex, int tileState) {
        int tmp = states[tileIndex];
        states[tileIndex] = tileState;

        return tmp;
    }

    @Override
    public void setTileStates(int tileIndexOffset, int[] tileStates) {
        System.arraycopy(tileStates, 0, states, tileIndexOffset, tileStates.length);
    }

    @Override
    public int getNeighbouringTileIndex(int tileIndex, int directionIndex) {
        switch (directionIndex) {
            case 0: return (tileIndex + 1) % size;
            case 1: return (tileIndex - width) % size;
            case 2: return (tileIndex - 1) % size;
            case 3: return (tileIndex + width) % size;
            default: throw new IllegalArgumentException("Invalid direction index.");
        }
    }

    @Override
    public SquareGrid clone() {
        SquareGrid cloned = new SquareGrid(width, height, size);

        System.arraycopy(this.states, 0, cloned.states, 0, size);

        return cloned;
    }
}

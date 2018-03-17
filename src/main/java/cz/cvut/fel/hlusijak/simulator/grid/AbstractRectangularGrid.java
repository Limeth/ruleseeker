package cz.cvut.fel.hlusijak.simulator.grid;

import com.google.common.base.Preconditions;

/**
 * A grid of squares.
 */
public abstract class AbstractRectangularGrid implements Grid {
    protected final int width, height, size;
    protected final int[] states;

    public AbstractRectangularGrid(int width, int height) {
        Preconditions.checkArgument(width > 0, "The width must be positive.");
        Preconditions.checkArgument(height > 0, "The height must be positive.");

        this.width = width;
        this.height = height;
        this.size = width * height;
        this.states = new int[size];
    }

    // For cloning
    protected AbstractRectangularGrid(int width, int height, int size) {
        this.width = width;
        this.height = height;
        this.size = size;
        this.states = new int[size];
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
}

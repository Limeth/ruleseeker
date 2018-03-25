package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import com.google.common.base.Preconditions;

/**
 * A grid of squares.
 */
public abstract class AbstractRectangularGridGeometry implements GridGeometry {
    protected final int width, height, size;

    public AbstractRectangularGridGeometry(int width, int height) {
        Preconditions.checkArgument(width > 0, "The width must be positive.");
        Preconditions.checkArgument(height > 0, "The height must be positive.");

        this.width = width;
        this.height = height;
        this.size = width * height;
    }

    @Override
    public int getSize() {
        return size;
    }
}

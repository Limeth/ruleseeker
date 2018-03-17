package cz.cvut.fel.hlusijak.simulator.grid;

import com.google.common.base.Preconditions;

/**
 * A grid of triangles.
 */
public abstract class TriangleGrid extends AbstractRectangularGrid {
    public TriangleGrid(int width, int height) {
        super(width, height);
    }

    private TriangleGrid(int width, int height, int size) {
        super(width, height, size);

        Preconditions.checkArgument(width % 2 == 0, "The width must be divisible by 2.");
        Preconditions.checkArgument(height % 2 == 0, "The width must be divisible by 2.");
    }
}

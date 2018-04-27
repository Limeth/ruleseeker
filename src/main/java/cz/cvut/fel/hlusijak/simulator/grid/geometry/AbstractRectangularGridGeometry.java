package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import com.google.common.base.Preconditions;

import cz.cvut.fel.hlusijak.util.Vector2i;

/**
 * A grid of squares.
 */
public abstract class AbstractRectangularGridGeometry implements GridGeometry {
    private final Vector2i dimensions;

    public AbstractRectangularGridGeometry(Vector2i dimensions) {
        Preconditions.checkArgument(dimensions.getX() > 0, "The width must be positive.");
        Preconditions.checkArgument(dimensions.getY() > 0, "The height must be positive.");

        this.dimensions = dimensions;
    }

    protected AbstractRectangularGridGeometry() {
        this.dimensions = null;
    }

    public Vector2i getDimensions() {
        return dimensions;
    }
}

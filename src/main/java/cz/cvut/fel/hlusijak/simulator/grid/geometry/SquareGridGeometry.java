package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import cz.cvut.fel.hlusijak.util.Vector2d;

/**
 * a grid of squares.
 *
 * example 4x4 grid:
 * |_|_|_|_|
 * |_|_|_|_|
 * |_|_|_|_|
 * |_|_|_|_|
 */
public class SquareGridGeometry extends AbstractRectangularGridGeometry {
    public SquareGridGeometry(int width, int height) {
        super(width, height);
    }

    @Override
    public int getNeighbourCount() {
        return 4;
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
    public Vector2d getVertexBoundingBox() {
        return Vector2d.of(width, height);
    }

    @Override
    public Vector2d[] getTileVertices(int tileIndex) {
        int x = tileIndex % width;
        int y = tileIndex / width;

        return new Vector2d[] {
            Vector2d.of(x, y),
            Vector2d.of(x + 1, y),
            Vector2d.of(x + 1, y + 1),
            Vector2d.of(x, y + 1),
        };
    }
}
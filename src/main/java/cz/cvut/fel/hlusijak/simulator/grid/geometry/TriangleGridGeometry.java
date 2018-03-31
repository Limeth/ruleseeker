package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import cz.cvut.fel.hlusijak.util.Vector2d;

/**
 * A grid of triangles.
 *
 * Example 4x4 grid:
 * /\/\/
 * \/\/\
 * /\/\/
 * \/\/\
 */
public class TriangleGridGeometry extends AbstractRectangularGridGeometry {
    private static final double TILE_HEIGHT = Math.sqrt(3) / 2;

    public TriangleGridGeometry(int width, int height) {
        super(width, height);
    }

    @Override
    public int getNeighbourCount() {
        return 3;
    }

    @Override
    public int getNeighbouringTileIndex(int tileIndex, int directionIndex) {
        int x = tileIndex % width;
        int y = tileIndex / width;
        boolean pointingUp = (x + y) % 2 == 0;

        if (pointingUp) {
            switch (directionIndex) {
                // Right
                case 0: return Math.floorMod(x + 1, width) + y * width;
                // Left
                case 1: return Math.floorMod(x - 1, width) + y * width;
                // Down
                case 2: return Math.floorMod(tileIndex + width, size);
            }
        } else {
            switch (directionIndex) {
                // Up
                case 0: return Math.floorMod(tileIndex - width, size);
                // Left
                case 1: return Math.floorMod(x - 1, width) + y * width;
                // Right
                case 2: return Math.floorMod(x + 1, width) + y * width;
            }
        }

        throw new RuntimeException("Unreachable.");
    }

    @Override
    public Vector2d getVertexBoundingBox() {
        return Vector2d.of(width / 2 + 0.5, height * TILE_HEIGHT);
    }

    @Override
    public Vector2d[] getTileVertices(int tileIndex) {
        int x = tileIndex % width;
        int y = tileIndex / width;
        boolean pointingUp = (x + y) % 2 == 0;
        double halfX = x / 2.0;

        if (pointingUp) {
            return new Vector2d[] {
                    Vector2d.of(halfX + 0.5, y * TILE_HEIGHT),
                    Vector2d.of(halfX, (y + 1) * TILE_HEIGHT),
                    Vector2d.of(halfX + 1, (y + 1) * TILE_HEIGHT),
            };
        } else {
            return new Vector2d[] {
                    Vector2d.of(halfX + 1, y * TILE_HEIGHT),
                    Vector2d.of(halfX, y * TILE_HEIGHT),
                    Vector2d.of(halfX + 0.5, (y + 1) * TILE_HEIGHT),
            };
        }
    }
}

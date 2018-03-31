package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import cz.cvut.fel.hlusijak.util.Vector2d;

/**
 * a grid of triangles.
 *
 * example 4x4 grid:
 * .-'-.-'-.-'-.-'-.
 * |   |   |   |   |
 * '-.-'-.-'-.-'-.-'-.
 *   |   |   |   |   |
 * .-'-.-'-.-'-.-'-.-'
 * |   |   |   |   |
 * '-.-'-.-'-.-'-.-'-.
 *   |   |   |   |   |
 *   '-.-'-.-'-.-'-.-'
 */
public class HexagonGridGeometry extends AbstractRectangularGridGeometry {
    private static final double TILE_WIDTH = Math.sqrt(3);

    public HexagonGridGeometry(int width, int height) {
        super(width, height);
    }

    @Override
    public int getNeighbourCount() {
        return 6;
    }

    @Override
    public int getNeighbouringTileIndex(int tileIndex, int directionIndex) {
        int x = tileIndex % width;
        int y = tileIndex / width;

        if (y % 2 == 0) {
            switch (directionIndex) {
                // Right
                case 0: return Math.floorMod(x + 1, width) + y * width;
                // Up-Right:
                case 1: return Math.floorMod(tileIndex - width, size);
                // Up-Left:
                case 2: return Math.floorMod(x - 1, width) + Math.floorMod(y - 1, height) * width;
                // Left
                case 3: return Math.floorMod(x - 1, width) + y * width;
                // Down-Left:
                case 4: return Math.floorMod(x - 1, width) + (y + 1) * width;
                // Down-Right:
                case 5: return tileIndex + width;
            }
        } else {
            switch (directionIndex) {
                // Right
                case 0: return Math.floorMod(x + 1, width) + y * width;
                // Up-Right:
                case 1: return Math.floorMod(x + 1, width) + (y - 1) * width;
                // Up-Left:
                case 2: return tileIndex - width;
                // Left
                case 3: return Math.floorMod(x - 1, width) + y * width;
                // Down-Left:
                case 4: return Math.floorMod(tileIndex + width, size);
                // Down-Right:
                case 5: return Math.floorMod(x + 1, width) + Math.floorMod(y + 1, height) * width;
            }
        }

        throw new IllegalArgumentException("Invalid direction index.");
    }

    @Override
    public Vector2d getVertexBoundingBox() {
        return Vector2d.of((width + 0.5) * TILE_WIDTH, height * 1.5 + 0.5);
    }

    @Override
    public Vector2d[] getTileVertices(int tileIndex) {
        int x = tileIndex % width;
        int y = tileIndex / width;
        double yCoordMultiplier = 1.5;
        double offsetX = y % 2 == 0 ? 0 : (TILE_WIDTH / 2);

        return new Vector2d[] {
                Vector2d.of(offsetX + (x + 1) * TILE_WIDTH, y * yCoordMultiplier + 0.5),
                Vector2d.of(offsetX + (x + 0.5) * TILE_WIDTH, y * yCoordMultiplier),
                Vector2d.of(offsetX + x * TILE_WIDTH, y * yCoordMultiplier + 0.5),
                Vector2d.of(offsetX + x * TILE_WIDTH, y * yCoordMultiplier + 1.5),
                Vector2d.of(offsetX + (x + 0.5) * TILE_WIDTH, y * yCoordMultiplier + 2.0),
                Vector2d.of(offsetX + (x + 1) * TILE_WIDTH, y * yCoordMultiplier + 1.5),
        };
    }
}

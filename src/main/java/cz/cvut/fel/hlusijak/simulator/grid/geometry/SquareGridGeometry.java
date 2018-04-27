package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import cz.cvut.fel.hlusijak.util.Vector2d;
import cz.cvut.fel.hlusijak.util.Vector2i;

import java.util.stream.IntStream;

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
    public SquareGridGeometry(Vector2i dimensions) {
        super(dimensions);
    }

    private SquareGridGeometry() {
        // Required by Kryo
    }

    @Override
    public int getEdgeNeighbourhoodSize() {
        return 4;
    }

    @Override
    public int getNeighbouringTileIndex(int tileIndex, int directionIndex) {
        int width = getDimensions().getX();
        int size = getSize();
        int y = tileIndex / width;

        switch (directionIndex) {
            // Right
            case 0: return Math.floorMod(tileIndex + 1, width) + y * width;
            // Up
            case 1: return Math.floorMod(tileIndex - width, size);
            // Left
            case 2: return Math.floorMod(tileIndex - 1, width) + y * width;
            // Down
            case 3: return Math.floorMod(tileIndex + width, size);
        }

        throw new IllegalArgumentException("Invalid direction index.");
    }

    @Override
    public Vector2d getVertexBoundingBox() {
        return getDimensions().floatingPoint();
    }

    @Override
    public Vector2d[] getTileVertices(int tileIndex) {
        int width = getDimensions().getX();
        int x = tileIndex % width;
        int y = tileIndex / width;

        return new Vector2d[] {
            Vector2d.of(x, y),
            Vector2d.of(x + 1, y),
            Vector2d.of(x + 1, y + 1),
            Vector2d.of(x, y + 1),
        };
    }

    @Override
    public int getVertexNeighbourhoodSize() {
        return 8;
    }

    @Override
    public IntStream vertexNeighbourhoodTileIndicesStream(int tileIndex) {
        return IntStream.of(
                getNeighbouringTileIndex(tileIndex, 0),
                getNeighbouringTileIndex(tileIndex, 0, 1),
                getNeighbouringTileIndex(tileIndex, 1),
                getNeighbouringTileIndex(tileIndex, 1, 2),
                getNeighbouringTileIndex(tileIndex, 2),
                getNeighbouringTileIndex(tileIndex, 2, 3),
                getNeighbouringTileIndex(tileIndex, 3),
                getNeighbouringTileIndex(tileIndex, 3, 0)
        );
    }
}

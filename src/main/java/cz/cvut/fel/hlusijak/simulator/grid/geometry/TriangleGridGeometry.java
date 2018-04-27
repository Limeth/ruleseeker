package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import com.google.common.base.Preconditions;
import cz.cvut.fel.hlusijak.util.Vector2d;
import cz.cvut.fel.hlusijak.util.Vector2i;

import java.util.stream.IntStream;

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

    public TriangleGridGeometry(Vector2i dimensions) {
        super(dimensions);
        Preconditions.checkArgument(dimensions.getX() % 2 == 0, "The width must be even.");
        Preconditions.checkArgument(dimensions.getY() % 2 == 0, "The height must be even.");
    }

    private TriangleGridGeometry() {
        // Required by Kryo
    }

    @Override
    public int getEdgeNeighbourhoodSize() {
        return 3;
    }

    @Override
    public int getNeighbouringTileIndex(int tileIndex, int directionIndex) {
        int width = getDimensions().getX();
        int size = getSize();
        int y = tileIndex / width;
        boolean pointingUp = (tileIndex + y) % 2 == 0;

        if (pointingUp) {
            switch (directionIndex) {
                // Right
                case 0: return Math.floorMod(tileIndex + 1, width) + y * width;
                // Left
                case 1: return Math.floorMod(tileIndex - 1, width) + y * width;
                // Down
                case 2: return Math.floorMod(tileIndex + width, size);
            }
        } else {
            switch (directionIndex) {
                // Left
                case 0: return Math.floorMod(tileIndex - 1, width) + y * width;
                // Right
                case 1: return Math.floorMod(tileIndex + 1, width) + y * width;
                // Up
                case 2: return Math.floorMod(tileIndex - width, size);
            }
        }

        throw new IllegalArgumentException("Invalid direction index.");
    }

    @Override
    public Vector2d getVertexBoundingBox() {
        Vector2i dimensions = getDimensions();

        return Vector2d.of(dimensions.getX() / 2 + 0.5, dimensions.getY() * TILE_HEIGHT);
    }

    @Override
    public Vector2d[] getTileVertices(int tileIndex) {
        int width = getDimensions().getX();
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

    @Override
    public int getVertexNeighbourhoodSize() {
        return 15;
    }

    @Override
    public IntStream vertexNeighbourhoodTileIndicesStream(int tileIndex) {
        //int y = tileIndex / width;
        //boolean pointingUp = (tileIndex + y) % 2 == 0;
        IntStream.Builder builder = IntStream.builder();

        for (int i = 0; i < 3; i++) {
            int dir0 = (i + 0) % 3;
            int dir1 = (i + 1) % 3;
            int dir2 = (i + 2) % 3;

            builder.add(getNeighbouringTileIndex(tileIndex, dir0, dir1));
            builder.add(getNeighbouringTileIndex(tileIndex, dir0));
            builder.add(getNeighbouringTileIndex(tileIndex, dir0, dir2));
            builder.add(getNeighbouringTileIndex(tileIndex, dir0, dir2, dir1));
            //builder.add(getNeighbouringTileIndex(tileIndex, dir0, dir2, dir1, dir2));

            /*
            builder.add(getNeighbouringTileIndex(tileIndex, dir1, dir0));
            builder.add(getNeighbouringTileIndex(tileIndex, dir0));
            builder.add(getNeighbouringTileIndex(tileIndex, dir2, dir0));
            builder.add(getNeighbouringTileIndex(tileIndex, dir1, dir2, dir0));
            //builder.add(getNeighbouringTileIndex(tileIndex, dir2, dir1, dir2, dir0));
            */
        }

        return builder.build();
    }
}

package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.util.Vector2d;
import cz.cvut.fel.hlusijak.util.Vector2i;

import java.util.stream.IntStream;

/**
 * Describes the geometric properties of a {@link Grid}.
 * All inheritors ought to be immutable.
 */
public interface GridGeometry {
    /**
     * @return Specifies the number of neighbours a tile has. This is also the
     *         number of directions one can use to traverse the grid.
     */
    int getEdgeNeighbourhoodSize();

    /**
     * @return The dimensions of the grid in a plane.
     */
    Vector2i getDimensions();

    /**
     * @return The total number of tiles in this grid.
     */
    default int getSize() {
        Vector2i dimensions = getDimensions();

        return dimensions.getX() * dimensions.getY();
    }

    /**
     * @param tileIndex The index of the current tile.
     * @param directionIndex The index of the direction to the neighbouring tile
     * @return The index of the neighbouring tile one unit in the direction of
     *         {@param directionIndex} relative to the position of the tile with
     *         index {@param tileIndex}.
     */
    int getNeighbouringTileIndex(int tileIndex, int directionIndex);

    /**
     * Like {@link #getNeighbouringTileIndex(int, int)}, but accepts an array of
     * direction indices to traverse the grid and return the final tile index.
     */
    default int getNeighbouringTileIndex(int tileIndex, int... directionIndices) {
        int result = tileIndex;

        for (int directionIndex : directionIndices) {
            result = getNeighbouringTileIndex(result, directionIndex);
        }

        return result;
    }

    /**
     * @return The smallest area spanning all vertices
     */
    Vector2d getVertexBoundingBox();

    /**
     * @param tileIndex The index of the tile to get the vertices of.
     * @return The vertices of the tile, so all tiles fit in the unit square.
     */
    Vector2d[] getTileVertices(int tileIndex);

    /**
     * @return A stream of all direction indices to traverse across tile edges.
     */
    default IntStream directionIndicesStream() {
        return IntStream.range(0, getEdgeNeighbourhoodSize());
    }

    /**
     * Generates a stream of tile indices of all tiles that share a common edge with the center tile.
     *
     * @param tileIndex The index of the center tile.
     * @return A stream of neighbouring tile indices in the order of directions.
     */
    default IntStream edgeNeighbourhoodTileIndicesStream(int tileIndex) {
        return directionIndicesStream().map(directionIndex -> getNeighbouringTileIndex(tileIndex, directionIndex));
    }

    /**
     * A generalized notion of Moore's neighbourhood is employed, where all cells
     * sharing at least one vertex with the center tile are deemed as in the
     * neighbourhood.
     *
     * @return The size of the Moore neighbourhood in this geometry
     */
    int getVertexNeighbourhoodSize();

    /**
     * A generalized notion of Moore's neighbourhood is employed, where all cells
     * sharing at least one vertex with the center tile are deemed as in the
     * neighbourhood.
     *
     * @param tileIndex The index of the center tile.
     * @return A stream of tile indices in Moore's neighbourhood of given {@param tileIndex}
     */
    IntStream vertexNeighbourhoodTileIndicesStream(int tileIndex);

    /**
     * Streams tile indices.
     */
    default IntStream tileIndexStream() {
        return IntStream.range(0, getSize());
    }
}

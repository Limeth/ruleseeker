package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.util.Vector2d;

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
    int getNeighbourCount();

    /**
     * @return The total number of tiles in this grid.
     */
    int getSize();

    /**
     * @param tileIndex The index of the current tile.
     * @param directionIndex The index of the direction to the neighbouring tile
     * @return The index of the neighbouring tile one unit in the direction of
     *         {@param directionIndex} relative to the position of the tile with
     *         index {@param tileIndex}.
     */
    int getNeighbouringTileIndex(int tileIndex, int directionIndex);

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
     * @return A stream of all direction indices.
     */
    default IntStream directionIndicesStream() {
        return IntStream.range(0, getNeighbourCount());
    }

    /**
     * @param tileIndex The index of the center tile.
     * @return A stream of neighbouring tile indices in the order of directions.
     */
    default IntStream neighbouringTileIndicesStream(int tileIndex) {
        return directionIndicesStream().map(directionIndex -> getNeighbouringTileIndex(tileIndex, directionIndex));
    }

    /**
     * Streams tile indices.
     */
    default IntStream tileIndexStream() {
        return IntStream.range(0, getSize());
    }
}

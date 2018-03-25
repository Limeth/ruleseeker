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
     * @param tileIndex The index of the center tile.
     * @return An array of neighbouring tile indices in the order of directions.
     */
    default int[] getNeighbouringTileIndices(int tileIndex) {
        int[] neighbouringIndices = new int[getNeighbourCount()];

        for (int directionIndex = 0, neighbourCount = getNeighbourCount();
             directionIndex < neighbourCount;
             directionIndex += 1) {
            neighbouringIndices[directionIndex] = getNeighbouringTileIndex(tileIndex, directionIndex);
        }

        return neighbouringIndices;
    }

    /**
     * Streams tile indices.
     */
    default IntStream tileIndexStream() {
        return IntStream.range(0, getSize());
    }
}

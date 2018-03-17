package cz.cvut.fel.hlusijak.simulator.grid;

/**
 * Describes the geometric properties of the cells and stores their state
 */
public interface Grid {
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
     * @param tileIndex The index of the tile to get the state index of.
     * @return The state index of the specified tile.
     */
    int getTileState(int tileIndex);

    /**
     * @param tileIndex The index of the tile to change the state index of.
     * @param tileState The new state index of the tile.
     * @return The previous state index of the specified tile.
     */
    int setTileState(int tileIndex, int tileState);

    /**
     * @param tileIndexOffset The index of the tile to start changing the state
     *                        indices from.
     * @param tileStates The new state indices.
     */
    void setTileStates(int tileIndexOffset, int[] tileStates);

    /**
     * @param tileIndex The index of the current tile.
     * @param directionIndex The index of the direction to the neighbouring tile
     * @return The index of the neighbouring tile one unit in the direction of
     *         {@param directionIndex} relative to the position of the tile with
     *         index {@param tileIndex}.
     */
    int getNeighbouringTileIndex(int tileIndex, int directionIndex);

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

    Grid clone();
}

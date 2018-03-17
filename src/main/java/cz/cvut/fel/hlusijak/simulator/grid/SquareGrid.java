package cz.cvut.fel.hlusijak.simulator.grid;

import cz.cvut.fel.hlusijak.util.Vector2d;

/**
 * A grid of squares.
 */
public class SquareGrid extends AbstractRectangularGrid {
    private final Vector2d vertexOffset;
    private final double edgeLength;

    public SquareGrid(int width, int height) {
        super(width, height);

        int max = Math.max(width, height);
        Vector2d unitDims = new Vector2d(width, height).div(new Vector2d(max));
        vertexOffset = Vector2d.ONE.sub(unitDims).div(2);
        edgeLength = 1 / (double) max;
    }

    private SquareGrid(int width, int height, int size, Vector2d vertexOffset, double edgeLength) {
        super(width, height, size);

        this.vertexOffset = vertexOffset;
        this.edgeLength = edgeLength;
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
    public Vector2d[] getTileVertices(int tileIndex) {
        return new Vector2d[0];
    }

    @Override
    public SquareGrid copy() {
        SquareGrid cloned = new SquareGrid(width, height, size, vertexOffset, edgeLength);

        System.arraycopy(this.states, 0, cloned.states, 0, size);

        return cloned;
    }
}

package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cz.cvut.fel.hlusijak.util.Vector2i;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SquareGridGeometryTest {
    /*
    cell indices:
    |0|1|2|
    |3|4|5|
    |6|7|8|

    direction indices:
    > ^ < v
    0 1 2 3
     */
    SquareGridGeometry geometry;

    @BeforeEach
    void setUp() {
        geometry = new SquareGridGeometry(Vector2i.of(3, 3));
    }

    @Test
    void getEdgeNeighbourhoodSize() {
        assertEquals(4, geometry.getEdgeNeighbourhoodSize());
    }

    @Test
    void getVertexNeighbourhoodSize() {
        assertEquals(8, geometry.getVertexNeighbourhoodSize());
    }

    @Test
    void getNeighbouringTileIndex() {
        final int RIGHT = 0, UP = 1, LEFT = 2, DOWN = 3;

        // Directions from the upper right corner
        assertEquals(0, geometry.getNeighbouringTileIndex(2, RIGHT));
        assertEquals(8, geometry.getNeighbouringTileIndex(2, UP));
        assertEquals(1, geometry.getNeighbouringTileIndex(2, LEFT));
        assertEquals(5, geometry.getNeighbouringTileIndex(2, DOWN));

        // Directions from the upper left corner
        assertEquals(1, geometry.getNeighbouringTileIndex(0, RIGHT));
        assertEquals(6, geometry.getNeighbouringTileIndex(0, UP));
        assertEquals(2, geometry.getNeighbouringTileIndex(0, LEFT));
        assertEquals(3, geometry.getNeighbouringTileIndex(0, DOWN));

        // Directions from the lower left corner
        assertEquals(7, geometry.getNeighbouringTileIndex(6, RIGHT));
        assertEquals(3, geometry.getNeighbouringTileIndex(6, UP));
        assertEquals(8, geometry.getNeighbouringTileIndex(6, LEFT));
        assertEquals(0, geometry.getNeighbouringTileIndex(6, DOWN));

        // Directions from the lower right corner
        assertEquals(6, geometry.getNeighbouringTileIndex(8, RIGHT));
        assertEquals(5, geometry.getNeighbouringTileIndex(8, UP));
        assertEquals(7, geometry.getNeighbouringTileIndex(8, LEFT));
        assertEquals(2, geometry.getNeighbouringTileIndex(8, DOWN));
    }

}
package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cz.cvut.fel.hlusijak.util.Vector2i;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TriangleGridGeometryTest {
    /*
    cell indices:
     /\1 /\3 /
    /0 \/2 \/
    \ 4/\ 6/\
     \/ 5\/ 7\
     /\9 /\11/
    /8 \/10\/
    \12/\14/\
     \/13\/15\

    direction indices if pointing up:
    > < v
    0 1 2

    direction indices if pointing down:
    < > ^
    0 1 2
     */
    TriangleGridGeometry geometry;

    @BeforeEach
    void setUp() {
        geometry = new TriangleGridGeometry(Vector2i.of(4, 4));
    }

    @Test
    void getEdgeNeighbourhoodSize() {
        assertEquals(3, geometry.getEdgeNeighbourhoodSize());
    }

    @Test
    void getVertexNeighbourhoodSize() {
        assertEquals(12, geometry.getVertexNeighbourhoodSize());
    }

    @Test
    void getNeighbouringTileIndex() {
        {
            final int RIGHT = 0, LEFT = 1, DOWN = 2;

            // Directions from the upper left corner
            assertEquals(1, geometry.getNeighbouringTileIndex(0, RIGHT));
            assertEquals(3, geometry.getNeighbouringTileIndex(0, LEFT));
            assertEquals(4, geometry.getNeighbouringTileIndex(0, DOWN));

            // Directions from the lower right corner
            assertEquals(12, geometry.getNeighbouringTileIndex(15, RIGHT));
            assertEquals(14, geometry.getNeighbouringTileIndex(15, LEFT));
            assertEquals(3, geometry.getNeighbouringTileIndex(15, DOWN));
        }

        {
            final int RIGHT = 1, UP = 2, LEFT = 0;

            // Directions from the upper right corner
            assertEquals(0, geometry.getNeighbouringTileIndex(3, RIGHT));
            assertEquals(15, geometry.getNeighbouringTileIndex(3, UP));
            assertEquals(2, geometry.getNeighbouringTileIndex(3, LEFT));

            // Directions from the lower left corner
            assertEquals(13, geometry.getNeighbouringTileIndex(12, RIGHT));
            assertEquals(8, geometry.getNeighbouringTileIndex(12, UP));
            assertEquals(15, geometry.getNeighbouringTileIndex(12, LEFT));
        }
    }

}
package cz.cvut.fel.hlusijak.simulator.grid.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cz.cvut.fel.hlusijak.util.Vector2i;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HexagonGridGeometryTest {
    /*
    cell indices:
    .-'-.-'-.-'-.-'-.
    | 0 | 1 | 2 | 3 |
    '-.-'-.-'-.-'-.-'-.
      | 4 | 5 | 6 | 7 |
    .-'-.-'-.-'-.-'-.-'
    | 8 | 9 |10 |11 |
    '-.-'-.-'-.-'-.-'-.
      |12 |13 |14 |15 |
      '-.-'-.-'-.-'-.-'

    direction indices:
    0 -- right
    1 -- up-right
    2 -- up-left
    3 -- left
    4 -- down-left
    5 -- down-right
     */
    HexagonGridGeometry geometry;

    @BeforeEach
    void setUp() {
        geometry = new HexagonGridGeometry(Vector2i.of(4, 4));
    }

    @Test
    void getEdgeNeighbourhoodSize() {
        assertEquals(6, geometry.getEdgeNeighbourhoodSize());
    }

    @Test
    void getVertexNeighbourhoodSize() {
        assertEquals(6, geometry.getVertexNeighbourhoodSize());
    }

    @Test
    void getNeighbouringTileIndex() {
        final int RIGHT = 0, UP_RIGHT = 1, UP_LEFT = 2, LEFT = 3, DOWN_LEFT = 4, DOWN_RIGHT = 5;

        // Directions from the upper right corner
        assertEquals(0, geometry.getNeighbouringTileIndex(3, RIGHT));
        assertEquals(15, geometry.getNeighbouringTileIndex(3, UP_RIGHT));
        assertEquals(14, geometry.getNeighbouringTileIndex(3, UP_LEFT));
        assertEquals(2, geometry.getNeighbouringTileIndex(3, LEFT));
        assertEquals(6, geometry.getNeighbouringTileIndex(3, DOWN_LEFT));
        assertEquals(7, geometry.getNeighbouringTileIndex(3, DOWN_RIGHT));

        // Directions from the upper left corner
        assertEquals(1, geometry.getNeighbouringTileIndex(0, RIGHT));
        assertEquals(12, geometry.getNeighbouringTileIndex(0, UP_RIGHT));
        assertEquals(15, geometry.getNeighbouringTileIndex(0, UP_LEFT));
        assertEquals(3, geometry.getNeighbouringTileIndex(0, LEFT));
        assertEquals(7, geometry.getNeighbouringTileIndex(0, DOWN_LEFT));
        assertEquals(4, geometry.getNeighbouringTileIndex(0, DOWN_RIGHT));

        // Directions from the lower left corner
        assertEquals(13, geometry.getNeighbouringTileIndex(12, RIGHT));
        assertEquals(9, geometry.getNeighbouringTileIndex(12, UP_RIGHT));
        assertEquals(8, geometry.getNeighbouringTileIndex(12, UP_LEFT));
        assertEquals(15, geometry.getNeighbouringTileIndex(12, LEFT));
        assertEquals(0, geometry.getNeighbouringTileIndex(12, DOWN_LEFT));
        assertEquals(1, geometry.getNeighbouringTileIndex(12, DOWN_RIGHT));

        // Directions from the lower right corner
        assertEquals(12, geometry.getNeighbouringTileIndex(15, RIGHT));
        assertEquals(8, geometry.getNeighbouringTileIndex(15, UP_RIGHT));
        assertEquals(11, geometry.getNeighbouringTileIndex(15, UP_LEFT));
        assertEquals(14, geometry.getNeighbouringTileIndex(15, LEFT));
        assertEquals(3, geometry.getNeighbouringTileIndex(15, DOWN_LEFT));
        assertEquals(0, geometry.getNeighbouringTileIndex(15, DOWN_RIGHT));
    }

}
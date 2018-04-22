package cz.cvut.fel.hlusijak.simulator.ruleset;

import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;

import java.util.stream.IntStream;

public class VertexSumRuleSet extends SumRuleSet<GridGeometry> {
    public VertexSumRuleSet(GridGeometry gridGeometry, int states, int[] rules) {
        super(gridGeometry, states, rules);
    }

    public VertexSumRuleSet(GridGeometry gridGeometry, int states) {
        super(gridGeometry, states, null);
    }

    @Override
    public int getNeighbourhoodSize() {
        return gridGeometry.getVertexNeighbourhoodSize();
    }

    @Override
    public IntStream neighbourhoodTileIndicesStream(int tileIndex) {
        return gridGeometry.vertexNeighbourhoodTileIndicesStream(tileIndex);
    }

    @Override
    public VertexSumRuleSet copy() {
        return new VertexSumRuleSet(gridGeometry, states, rules);
    }
}

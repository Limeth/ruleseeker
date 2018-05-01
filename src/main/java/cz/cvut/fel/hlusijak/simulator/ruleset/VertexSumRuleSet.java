package cz.cvut.fel.hlusijak.simulator.ruleset;

import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;

import java.util.Arrays;
import java.util.stream.IntStream;

public class VertexSumRuleSet extends SumRuleSet<GridGeometry> {
    public VertexSumRuleSet(GridGeometry gridGeometry, byte states, byte[] rules) {
        super(gridGeometry, states, rules);
    }

    public VertexSumRuleSet(GridGeometry gridGeometry, byte states) {
        super(gridGeometry, states, null);
    }

    private VertexSumRuleSet() {
        // Required by Kryo
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
        return new VertexSumRuleSet(gridGeometry, states, Arrays.copyOf(rules, rules.length));
    }
}

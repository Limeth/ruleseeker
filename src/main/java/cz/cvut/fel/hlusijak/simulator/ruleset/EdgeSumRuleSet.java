package cz.cvut.fel.hlusijak.simulator.ruleset;

import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;

import java.util.Arrays;
import java.util.stream.IntStream;

public class EdgeSumRuleSet extends SumRuleSet<GridGeometry> {
    public EdgeSumRuleSet(GridGeometry gridGeometry, int states, int[] rules) {
        super(gridGeometry, states, rules);
    }

    public EdgeSumRuleSet(GridGeometry gridGeometry, int states) {
        super(gridGeometry, states, null);
    }

    @Override
    public int getNeighbourhoodSize() {
        return this.gridGeometry.getEdgeNeighbourhoodSize();
    }

    @Override
    public IntStream neighbourhoodTileIndicesStream(int tileIndex) {
        return this.gridGeometry.edgeNeighbourhoodTileIndicesStream(tileIndex);
    }

    @Override
    public EdgeSumRuleSet copy() {
        return new EdgeSumRuleSet(gridGeometry, states, Arrays.copyOf(rules, rules.length));
    }
}

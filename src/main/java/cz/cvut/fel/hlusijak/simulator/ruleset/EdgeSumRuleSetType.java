package cz.cvut.fel.hlusijak.simulator.ruleset;

import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;

import java.util.stream.IntStream;

/**
 * A rule set that assigns a rule for each combination of neighbouring tile states.
 *
 * Any cells sharing the edges with the current cell are considered in the neighbourhood.
 */
public class EdgeSumRuleSetType extends SumRuleSetType<GridGeometry> {
    public EdgeSumRuleSetType(GridGeometry gridGeometry, byte states) {
        super(gridGeometry, states);
    }

    private EdgeSumRuleSetType() {
        // Required by Kryo
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
    public EdgeSumRuleSetType copy() {
        return new EdgeSumRuleSetType(gridGeometry, states);
    }

    @Override
    public String toString() {
        return "EdgeSumRuleSetType{" +
                "gridGeometry=" + gridGeometry +
                ", states=" + states +
                ", neighbouringStateCombinations=" + neighbouringStateCombinations +
                ", ruleSetSize=" + ruleSetSize +
                '}';
    }
}

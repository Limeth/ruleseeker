package cz.cvut.fel.hlusijak.simulator.ruleset;

import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;

import java.util.stream.IntStream;

/**
 * A rule set that assigns a rule for each combination of neighbouring tile states.
 *
 * Any cells sharing the vertices with the current cell are considered in the neighbourhood.
 */
public class VertexSumRuleSetType extends SumRuleSetType<GridGeometry> {
    public VertexSumRuleSetType(GridGeometry gridGeometry, byte states) {
        super(gridGeometry, states);
    }

    private VertexSumRuleSetType() {
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
    public VertexSumRuleSetType copy() {
        return new VertexSumRuleSetType(gridGeometry, states);
    }

    @Override
    public String toString() {
        return "VertexSumRuleSetType{" +
                "gridGeometry=" + gridGeometry +
                ", states=" + states +
                ", neighbouringStateCombinations=" + neighbouringStateCombinations +
                ", ruleSetSize=" + ruleSetSize +
                '}';
    }
}

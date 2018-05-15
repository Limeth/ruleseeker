package cz.cvut.fel.hlusijak.simulator.ruleset;

import com.google.common.base.Preconditions;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A rule set that assigns a rule for each combination of neighbouring tile states.
 */
public abstract class SumRuleSetType<G extends GridGeometry> implements RuleSetType {
    protected final G gridGeometry;
    protected final byte states;
    protected final int neighbouringStateCombinations;
    protected final int ruleSetSize;

    public SumRuleSetType(G gridGeometry, byte states) {
        Preconditions.checkNotNull(gridGeometry);
        Preconditions.checkArgument(states >= 2, "The number of states must be at least 2.");

        this.gridGeometry = gridGeometry;
        this.states = states;
        this.neighbouringStateCombinations = Math.toIntExact(combinationsWithRepetitions(getNeighbourhoodSize(), states));
        this.ruleSetSize = states * neighbouringStateCombinations;
    }

    protected SumRuleSetType() {
        // Required by Kryo
        this.gridGeometry = null;
        this.states = 0;
        this.neighbouringStateCombinations = 0;
        this.ruleSetSize = 0;
    }

    /**
     * @return The total number of cells in the neighbourhood.
     */
    public abstract int getNeighbourhoodSize();

    /**
     * @param tileIndex The index of the cell to get the stream of neighbours of.
     * @return A stream of indices of the tiles neighbouring with the cell with the index {@param tileIndex}.
     */
    public abstract IntStream neighbourhoodTileIndicesStream(int tileIndex);

    @Override
    public G getGridGeometry() {
        return gridGeometry;
    }

    @Override
    public byte getNumberOfStates() {
        return states;
    }

    @Override
    public int getRuleSetSize() {
        return ruleSetSize;
    }

    private int[] countNeighbouringStates(Grid grid, int tileIndex) {
        int[] stateCount = new int[states];

        neighbourhoodTileIndicesStream(tileIndex)
                .map(grid::getTileState)
                .forEach(state -> stateCount[state] += 1);

        return stateCount;
    }

    @Override
    public int getRuleIndex(Grid grid, int tileIndex) {
        byte state = grid.getTileState(tileIndex);
        int[] neighbouringStateCount = countNeighbouringStates(grid, tileIndex);

        return state * this.neighbouringStateCombinations + combinationIndexWithRepetition(neighbouringStateCount);
    }

    /**
     * @param ruleSet The rule set of the simulation.
     * @return A stream of the rules.
     */
    public Stream<SumRuleRecord> enumerateRules(RuleSet ruleSet) {
        final int neighbourhoodSize = getNeighbourhoodSize();

        return IntStream.range(0, ruleSetSize).boxed()
            .map(index -> {
                byte previousState = (byte) (index / this.neighbouringStateCombinations);
                int combinationIndex = index % this.neighbouringStateCombinations;
                int[] stateCount = combinationWithRepetition(neighbourhoodSize, states, combinationIndex);

                return new SumRuleRecord(index, previousState, stateCount, ruleSet.getRule(index));
            });
    }

    private static long binomial(int n, int k) {
        if (k > n - k) {
            k = n - k;
        }

        long result = 1;

        for (int denominator = 1, numerator = n; denominator <= k; denominator++, numerator--) {
            result = result * numerator / denominator;
        }

        return result;
    }

    private static long combinationsWithRepetitions(int k, int n) {
        return binomial(k + n - 1, k);
    }

    private static void combinationWithRepetitionInner(int k, int n, int index, int[] stateCount) {
        Integer foundState = null;
        int indexOffset = 0;
        int totalStateCombos = 0;

        for (int state = 0; state < n; state += 1) {
            int stateCombos = (int) combinationsWithRepetitions(k - 1, n - state);
            indexOffset = totalStateCombos;
            totalStateCombos += stateCombos;

            if (index < totalStateCombos) {
                foundState = state;
                break;
            }
        }

        Preconditions.checkNotNull(foundState, "Combination index out of bounds.");

        stateCount[foundState + stateCount.length - n] += 1;

        if (k > 1) {
            combinationWithRepetitionInner(k - 1, n - foundState, index - indexOffset, stateCount);
        }
    }

    /**
     * @param k Number of selected items
     * @param n Number of different states to select from
     * @param index Index of the combination with repetition to return
     * @return The combination at the given {@param index} represented by the count of each state at its index in the returned array
     */
    private static int[] combinationWithRepetition(int k, int n, int index) {
        Preconditions.checkArgument(k > 0, "The combination class (k) must be positive.");
        Preconditions.checkArgument(n > 0, "The number of states (n) must be positive.");
        int[] stateCount = new int[n];

        combinationWithRepetitionInner(k, n, index, stateCount);

        return stateCount;
    }

    private static int combinationIndexWithRepetitionInner(int[] stateCount, int k) {
        int n = stateCount.length;
        int index = 0;
        int statesLeft = k;

        for (int state = 0; state < n - 1; state += 1) {
            statesLeft -= stateCount[state];

            if (statesLeft <= 0) {
                break;
            }

            index += combinationsWithRepetitions(statesLeft - 1, n - state);
        }

        return index;
    }

    /**
     * @param stateCount The combination represented by the count of each state at its index in the returned array
     * @return The index of the given combination with repetition
     */
    private static int combinationIndexWithRepetition(int[] stateCount) {
        int k = Arrays.stream(stateCount).sum();

        return combinationIndexWithRepetitionInner(Arrays.copyOf(stateCount, stateCount.length), k);
    }
}

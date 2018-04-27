package cz.cvut.fel.hlusijak.simulator.ruleset;

import com.google.common.base.Preconditions;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.SumRuleRecord;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class SumRuleSet<G extends GridGeometry> implements RuleSet {
    protected final G gridGeometry;
    protected final int states;
    protected final int neighbouringStateCombinations;
    protected int[] rules;

    public SumRuleSet(G gridGeometry, int states, int[] rules) {
        Preconditions.checkNotNull(gridGeometry);
        Preconditions.checkArgument(states >= 2, "The number of states must be at least 2.");

        this.gridGeometry = gridGeometry;
        this.states = states;
        this.neighbouringStateCombinations = Math.toIntExact(combinationsWithRepetitions(getNeighbourhoodSize(), states));
        int requiredRulesLen = states * neighbouringStateCombinations;

        if (rules == null) {
            rules = new int[requiredRulesLen];
        } else {
            Preconditions.checkArgument(rules.length == requiredRulesLen,
                    String.format("The rules array must be of length %d, but is of length %d", requiredRulesLen, rules.length));
        }

        this.rules = rules;
    }

    public SumRuleSet(G gridGeometry, int states) {
        this(gridGeometry, states, null);
    }

    protected SumRuleSet() {
        // Required by Kryo
        this.gridGeometry = null;
        this.states = 0;
        this.neighbouringStateCombinations = 0;
    }

    public abstract int getNeighbourhoodSize();
    public abstract IntStream neighbourhoodTileIndicesStream(int tileIndex);

    public int[] getRules() {
        return rules;
    }

    public void setRules(int[] rules) {
       Preconditions.checkArgument(rules.length == this.rules.length,
               String.format("The rules array must be of length %d, but is of length %d", this.rules.length, rules.length));

       this.rules = rules;
    }

    @Override
    public int getNumberOfStates() {
        return states;
    }

    private int[] countNeighbouringStates(Grid grid, int tileIndex) {
        int[] stateCount = new int[states];

        neighbourhoodTileIndicesStream(tileIndex)
                .map(grid::getTileState)
                .forEach(state -> stateCount[state] += 1);

        return stateCount;
    }

    @Override
    public int getNextTileState(Grid grid, int tileIndex) {
        int state = grid.getTileState(tileIndex);
        int[] neighbouringStateCount = countNeighbouringStates(grid, tileIndex);
        int ruleIndex = state * this.neighbouringStateCombinations + combinationIndexWithRepetition(neighbouringStateCount);

        return rules[ruleIndex];
    }

    public Stream<SumRuleRecord> enumerateRules() {
        final int neighbourhoodSize = getNeighbourhoodSize();

        return IntStream.range(0, rules.length).boxed()
            .map(index -> {
                int previousState = index / this.neighbouringStateCombinations;
                int combinationIndex = index % this.neighbouringStateCombinations;
                int[] stateCount = combinationWithRepetition(neighbourhoodSize, states, combinationIndex);

                return new SumRuleRecord(index, previousState, stateCount, rules[index]);
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

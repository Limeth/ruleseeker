package cz.cvut.fel.hlusijak.simulator.ruleset;

import com.google.common.base.Preconditions;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;

import java.util.Arrays;

public class SumRuleSet implements RuleSet {
    private final GridGeometry gridGeometry;
    private final int states;
    private final int[] rules;

    public SumRuleSet(GridGeometry gridGeometry, int states, int[] rules) {
        Preconditions.checkNotNull(gridGeometry);
        Preconditions.checkArgument(states >= 2, "The number of states must be at least 2.");

        int requiredRulesLen = Math.toIntExact(combinationsWithRepetitions(gridGeometry.getNeighbourCount(), states));

        if (rules == null) {
            rules = new int[requiredRulesLen];
        } else {
            Preconditions.checkArgument(rules.length == requiredRulesLen,
                    String.format("The rules array must be of length %d, but is of length %d", requiredRulesLen, rules.length));
        }

        this.gridGeometry = gridGeometry;
        this.states = states;
        this.rules = rules;
    }

    @Override
    public int getNumberOfStates() {
        return states;
    }

    @Override
    public int getNextTileState(Grid grid, int tileIndex) {
        return 0;
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

    public static long combinationsWithRepetitions(int k, int n) {
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
    public static int[] combinationWithRepetition(int k, int n, int index) {
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

        for (int state = 0; state < stateCount.length - 1; state += 1) {
            statesLeft -= stateCount[state];

            if (statesLeft <= 0) {
                break;
            }

            index += combinationsWithRepetitions(statesLeft - 1, stateCount.length - state);
        }

        return index;
    }

    /**
     * @param stateCount The combination represented by the count of each state at its index in the returned array
     * @return The index of the given combination with repetition
     */
    public static int combinationIndexWithRepetition(int[] stateCount) {
        int k = Arrays.stream(stateCount).sum();
        int n = stateCount.length;

        return combinationIndexWithRepetitionInner(Arrays.copyOf(stateCount, stateCount.length), k);
    }
}

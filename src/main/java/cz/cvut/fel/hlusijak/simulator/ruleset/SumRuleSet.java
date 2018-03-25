package cz.cvut.fel.hlusijak.simulator.ruleset;

import com.google.common.base.Preconditions;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;

public class SumRuleSet implements RuleSet {
    private final GridGeometry gridGeometry;
    private final int states;

    public SumRuleSet(GridGeometry gridGeometry, int states) {
        Preconditions.checkNotNull(gridGeometry);
        Preconditions.checkArgument(states >= 2, "The number of states must be at least 2.");

        this.gridGeometry = gridGeometry;
        this.states = states;
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

    /*
    public static int[] combinationWithRepetition(int ofClass, int elements, int combinationIndex) {
        int[] combination = new int[ofClass];
        long k = 0;

        for (int i = 0; i < ofClass - 1; i++) {
            combination[i] = i != 0 ? combination[i - 1] : 0;
            long r;

            do {
                combination[i]++;
                r = binomial(elements - combination[i], ofClass - (i + 1));
                k += r;
            } while (k < combinationIndex);

            k -= r;
        }

        combination[ofClass - 1] = combination[ofClass - 2] + combinationIndex - (int) k;

        return combination;
    }
    */

    private static void combinationWithRepetitionInner(int k, int n, int index, int[] result, int originalN) {
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

        int writeIndex = result.length - k;
        result[writeIndex] = foundState + originalN - n;

        if (k > 1) {
            combinationWithRepetitionInner(k - 1, n - foundState, index - indexOffset, result, originalN);
        }
    }

    public static int[] combinationWithRepetition(int k, int n, int index) {
        Preconditions.checkArgument(k > 0, "The combination class (k) must be positive.");
        Preconditions.checkArgument(n > 0, "The number of states (n) must be positive.");
        int[] result = new int[k];

        combinationWithRepetitionInner(k, n, index, result, n);

        return result;
    }
}

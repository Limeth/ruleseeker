package cz.cvut.fel.hlusijak.simulator.ruleset;

public class SumRuleRecord {
    private final int index;
    private final int previousState;
    private final int[] stateCount;
    private final int nextState;

    public SumRuleRecord(int index, int previousState, int[] stateCount, int nextState) {
        this.index = index;
        this.previousState = previousState;
        this.stateCount = stateCount;
        this.nextState = nextState;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the previousState
     */
    public int getPreviousState() {
        return previousState;
    }

    /**
     * @return the stateCount
     */
    public int[] getStateCount() {
        int[] copied = new int[stateCount.length];

        System.arraycopy(stateCount, 0, copied, 0, stateCount.length);

        return copied;
    }

    /**
     * @return the nextState
     */
    public int getNextState() {
        return nextState;
    }
}

package cz.cvut.fel.hlusijak.simulator.ruleset;

public class SumRuleRecord {
    private final int index;
    private final byte previousState;
    private final int[] stateCount;
    private final byte nextState;

    public SumRuleRecord(int index, byte previousState, int[] stateCount, byte nextState) {
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
    public byte getPreviousState() {
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
    public byte getNextState() {
        return nextState;
    }
}

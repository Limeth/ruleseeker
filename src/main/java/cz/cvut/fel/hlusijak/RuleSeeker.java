package cz.cvut.fel.hlusijak;

import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.ruleset.SumRuleSet;

import java.util.Arrays;
import java.util.Random;

public class RuleSeeker {
    private static RuleSeeker instance;
    private Simulator simulator;

    public RuleSeeker() {
        Random rng = new Random();
        //this.simulator = new Simulator(new SquareGridGeometry(10, 15), (grid, tileIndex) -> rng.nextInt(2), 10);
    }

    public Simulator getSimulator() {
        return simulator;
    }

    public static void main(String[] args) {
        int total = (int) SumRuleSet.combinationsWithRepetitions(4, 3);

        for (int i = 0; i <= total; i++) {
            System.out.println(Arrays.toString(SumRuleSet.combinationWithRepetition(4, 3, i)));
        }
        //instance = new RuleSeeker();
        //Application.launch(SimulatorApplication.class, args);
    }

    public static RuleSeeker getInstance() {
        return instance;
    }
}

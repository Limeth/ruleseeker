package cz.cvut.fel.hlusijak;

import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.SimulatorApplication;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.SquareGridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.simulator.ruleset.SumRuleSet;
import javafx.application.Application;

import java.util.Random;

public class RuleSeeker {
    private static RuleSeeker instance;
    private Simulator simulator;

    public RuleSeeker() {
        Random rng = new Random();
        GridGeometry gridGeometry = new SquareGridGeometry(10, 15);
        RuleSet ruleSet = new SumRuleSet(gridGeometry, 2);

        ruleSet.randomizeRules(rng);

        Grid grid = new Grid(gridGeometry);

        grid.randomizeTileStates(rng, ruleSet);

        this.simulator = new Simulator(grid, ruleSet, 10);
    }

    public Simulator getSimulator() {
        return simulator;
    }

    public static void main(String[] args) {
        instance = new RuleSeeker();
        Application.launch(SimulatorApplication.class, args);
    }

    public static RuleSeeker getInstance() {
        return instance;
    }
}

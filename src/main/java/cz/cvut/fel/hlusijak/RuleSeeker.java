package cz.cvut.fel.hlusijak;

import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.SimulatorApplication;
import cz.cvut.fel.hlusijak.simulator.grid.SquareGrid;
import javafx.application.Application;

import java.util.Random;

public class RuleSeeker {
    private static RuleSeeker instance;
    private Simulator simulator;

    public RuleSeeker() {
        Random rng = new Random();
        this.simulator = new Simulator(new SquareGrid(10, 15), (grid, tileIndex) -> rng.nextInt(2), 10);
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

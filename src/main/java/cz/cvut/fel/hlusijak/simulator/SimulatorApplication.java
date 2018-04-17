package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.HexagonGridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.simulator.ruleset.SumRuleSet;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Random;

public class SimulatorApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        initSimulator();

        Parent root = FXMLLoader.load(getClass().getResource("template.fxml"));

        primaryStage.setTitle(RuleSeeker.getInstance().getProjectArtifactId());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initSimulator() {
        Random rng = new Random();
        GridGeometry gridGeometry = new HexagonGridGeometry(100, 100);
        RuleSet ruleSet = new SumRuleSet(gridGeometry, 4);

        ruleSet.randomizeRules(rng);

        Grid grid = new Grid(gridGeometry);

        grid.randomizeTileStates(rng, ruleSet);

        Simulator simulator = new Simulator(grid, ruleSet, 10);

        RuleSeeker.getInstance().setSimulator(simulator);
    }
}

package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.TriangleGridGeometry;
import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.StateColoringMethod;
import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.HueStateColoringMethod;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.simulator.ruleset.VertexSumRuleSet;
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
        GridGeometry gridGeometry = new TriangleGridGeometry(128, 64);
        RuleSet ruleSet = new VertexSumRuleSet(gridGeometry, 4);

        ruleSet.randomizeRules(rng);

        Grid grid = new Grid(gridGeometry);

        grid.randomizeTileStates(rng, ruleSet);

        StateColoringMethod stateColoringMethod = new HueStateColoringMethod();
        Simulator simulator = new Simulator(grid, ruleSet, stateColoringMethod, 10);

        RuleSeeker.getInstance().setSimulator(simulator);
    }
}

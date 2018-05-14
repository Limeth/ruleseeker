package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.SquareGridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.simulator.ruleset.VertexSumRuleSetType;
import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.HueStateColoringMethod;
import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.StateColoringMethod;
import cz.cvut.fel.hlusijak.util.Vector2i;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;

/**
 * A JavaFX application to visualise the progress of a simulation.
 */
public class SimulatorApplication extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorApplication.class);
    private static boolean JFX_INITIALIZED = false;

    @Override
    public void init() {
        JFX_INITIALIZED = true;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initSimulator();

        Parent root = FXMLLoader.load(getClass().getResource("template.fxml"));
        SimulatorController controller = new SimulatorController(primaryStage);

        try {
            FXMLLoader loader = new FXMLLoader(SettingsDialog.class.getResource("template.fxml"));
            loader.setController(controller);
            root = loader.load();
        } catch (IOException e) {
            LOGGER.error("An exception occurred while opening the settings dialog.", e);
        }

        primaryStage.setTitle(RuleSeeker.getInstance().getProjectArtifactId());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initSimulator() {
        Random rng = new Random();
        GridGeometry gridGeometry = new SquareGridGeometry(Vector2i.of(50, 50));
        RuleSet ruleSet = new RuleSet(new VertexSumRuleSetType(gridGeometry, (byte) 2));

        ruleSet.randomizeRules(rng);

        Grid grid = new Grid(gridGeometry);

        grid.randomizeTileStates(rng, ruleSet);

        StateColoringMethod stateColoringMethod = HueStateColoringMethod.random();
        Simulator simulator = new Simulator(grid, ruleSet, stateColoringMethod);

        RuleSeeker.getInstance().setSimulator(simulator);
    }

    /**
     * @return Whether {@link SimulatorApplication} has been instantiated and initialized.
     */
    public static boolean isJFXInitialized() {
        return JFX_INITIALIZED;
    }
}

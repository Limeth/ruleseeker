package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.util.Vector2d;
import cz.cvut.fel.hlusijak.util.Wrapper;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.javatuples.Pair;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SimulatorController implements Initializable {
    @FXML private Pane viewPane;

    // Settings tab
    @FXML private Tab settingsTab;
    @FXML private Button importRulesOrStateButton;
    @FXML private Button exportStateButton;
    @FXML private Spinner<Integer> widthSpinner;
    @FXML private Spinner<Integer> heightSpinner;
    @FXML private CheckBox customStateColorsCheckBox;
    @FXML private HBox customStateColorsHBox;
    @FXML private ChoiceBox<Integer> customStateColorsStateChoiceBox;
    @FXML private ColorPicker customStateColorsColorPicker;

    // Simulation tab
    @FXML private Button resumeButton;
    @FXML private Button stepButton;
    @FXML private Button pauseButton;
    @FXML private TextField intervalTextField;
    @FXML private Slider intervalSlider;
    @FXML private ChoiceBox<Integer> editModeChoiceBox;
    @FXML private Button fillButton;
    @FXML private Button randomizeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewPane.layoutBoundsProperty()
                .addListener(observable -> updateViewPane(null));

        // Workaround for the previous listener not being triggered in the first frame
        Wrapper<InvalidationListener> firstRenderListener = new Wrapper<>(null);
        firstRenderListener.value = observable -> {
            observable.removeListener(firstRenderListener.value);
            updateViewPane(null);
        };
        viewPane.heightProperty().addListener(firstRenderListener.value);

        stepButton.setOnAction(event -> {
            Simulator simulator = RuleSeeker.getInstance().getSimulator();

            try {
                simulator.nextIteration().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            updateViewPane(null);
        });

        randomizeButton.setOnAction(event -> {
            Simulator simulator = RuleSeeker.getInstance().getSimulator();

            synchronized (simulator) {
                Grid grid = simulator.getGrid();

                grid.randomizeTileStates(new Random(), simulator.getRuleSet());

                simulator.setGrid(grid);
            }

            updateViewPane(null);
        });

        resumeButton.setOnAction(event -> {
            Simulator simulator = RuleSeeker.getInstance().getSimulator();

            simulator.runAsync(iteration -> {
                Platform.runLater(() -> updateViewPane(simulator.getGrid()));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return iteration % 10 != 0;
            });
        });
    }

    private Paint getCellColor(int state) {
        if (state == 0) {
            return Color.WHITE;
        }

        int remainingStates = RuleSeeker.getInstance().getSimulator().getRuleSet().getNumberOfStates() - 1;
        state = state % remainingStates;

        return Color.hsb(360.0 * state / (double) remainingStates, 1.0, 0.75);
    }

    private Pair<Double, Vector2d> createTransformation() {
        Simulator simulator = RuleSeeker.getInstance().getSimulator();
        Grid grid = simulator.getGrid();
        Vector2d boundingBox = grid.getGeometry().getVertexBoundingBox();
        Vector2d paneSize = Vector2d.of(viewPane.getWidth(), viewPane.getHeight());
        Vector2d ratio = paneSize.div(boundingBox);
        double scale = ratio.min();
        Vector2d scaledBoundingBox = boundingBox.mul(scale);
        Vector2d offset = paneSize.sub(scaledBoundingBox).div(2);

        return Pair.with(scale, offset);
    }

    public void updateViewPane(Grid grid) {
        final Grid finalGrid;

        if (grid == null) {
            Simulator simulator = RuleSeeker.getInstance().getSimulator();
            finalGrid = simulator.getGrid();
        } else {
            finalGrid = grid;
        }

        GridGeometry gridGeometry = finalGrid.getGeometry();
        Pair<Double, Vector2d> transformation = createTransformation();
        double scale = transformation.getValue0();
        Vector2d offset = transformation.getValue1();

        viewPane.getChildren().clear();

        gridGeometry.tileIndexStream().forEachOrdered(tileIndex -> {
            List<Vector2d> tileVertices = Arrays.stream(gridGeometry.getTileVertices(tileIndex))
                    .map(vertex -> vertex.mul(scale))
                    .map(offset::add)
                    .collect(Collectors.toList());
            int state = finalGrid.getTileState(tileIndex);
            Paint fill = getCellColor(state);
            CellShape cellShape = new CellShape(fill, tileVertices.toArray(new Vector2d[tileVertices.size()]));

            viewPane.getChildren().add(cellShape);
        });
    }
}

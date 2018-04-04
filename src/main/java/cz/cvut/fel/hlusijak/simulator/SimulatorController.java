package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.util.FutureUtil;
import cz.cvut.fel.hlusijak.util.TimeUtil;
import cz.cvut.fel.hlusijak.util.Vector2d;
import cz.cvut.fel.hlusijak.util.Wrapper;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.javatuples.Pair;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
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
    @FXML private ComboBox<Integer> editModeComboBox;
    @FXML private Button fillButton;
    @FXML private Button randomizeButton;

    // Synchronizes access to the following fields.
    private final Object simulationLock = new Object();
    private boolean resumed;
    private boolean resumeTaskRunning;
    private boolean ignoreIntervalEvents;
    private double intervalSeconds = 1.0;

    private double hueOffset = 0; // Update whenever the rule set changes

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hueOffset = RuleSeeker.getInstance().getSimulator().getRuleSet().getHueOffset();

        initializeView();
        initializeSimulationTab();
    }

    private void initializeView() {
        viewPane.layoutBoundsProperty()
                .addListener(observable -> updateViewPane(null));

        // Workaround for the previous listener not being triggered in the first frame
        Wrapper<InvalidationListener> firstRenderListener = new Wrapper<>(null);
        firstRenderListener.value = observable -> {
            observable.removeListener(firstRenderListener.value);
            updateViewPane(null);
        };
        viewPane.heightProperty().addListener(firstRenderListener.value);
    }

    private void initializeSimulationTab() {
        Simulator simulator = RuleSeeker.getInstance().getSimulator();

        resumeButton.setOnAction(event -> {
            synchronized (simulationLock) {
                resumed = true;

                if (resumeTaskRunning) {
                    return;
                }

                resumeTaskRunning = true;
                simulator.runAsync(this::onIterationComplete);
            }
        });

        stepButton.setOnAction(event -> {
            simulator.nextIteration().thenAcceptAsync(iteration ->
                updateViewPane(null), FutureUtil.getJFXExecutor());
        });

        pauseButton.setOnAction(event -> {
            synchronized (simulationLock) {
                resumed = false;
            }
        });

        intervalTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                synchronized (simulationLock) {
                    if (this.ignoreIntervalEvents) {
                        return;
                    }

                    this.intervalSeconds = Double.parseDouble(newValue);
                    this.ignoreIntervalEvents = true;
                    intervalSlider.setValue(Math.max(Math.min(intervalSeconds * 100, 100.0), 0));
                    this.ignoreIntervalEvents = false;
                }
            } catch (NumberFormatException e) {
                // Don't change the value
            }
        });

        intervalTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                synchronized (simulationLock) {
                    intervalTextField.setText(Double.toString(this.intervalSeconds));
                }
            }
        });

        intervalSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            synchronized (simulationLock) {
                if (ignoreIntervalEvents) {
                    return;
                }

                this.intervalSeconds = newValue.doubleValue() / 100.0;
                this.intervalSeconds = Math.floor(this.intervalSeconds * 100) / 100;
                this.ignoreIntervalEvents = true;

                intervalTextField.setText(Double.toString(this.intervalSeconds));

                this.ignoreIntervalEvents = false;
            }
        });

        intervalTextField.setText(Double.toString(this.intervalSeconds));

        editModeComboBox.setCellFactory(listView -> {
            ListCell<Integer> listCell = new ListCell<Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        return;
                    }

                    setText(Integer.toString(item));
                    setBackground(new Background(new BackgroundFill(getCellColor(item), null, null)));
                }
            };


            return listCell;
        });

        editModeComboBox.valueProperty().addListener(((observable, oldValue, newValue) -> {
            editModeComboBox.setBackground(new Background(new BackgroundFill(getCellColor(newValue), null, null)));
        }));

        RuleSeeker.getInstance().getSimulator().getRuleSet().stateStream().forEach(editModeComboBox.itemsProperty().get()::add);
        editModeComboBox.setValue(editModeComboBox.getItems().get(0));

        randomizeButton.setOnAction(event -> {
            synchronized (simulator) {
                Grid grid = simulator.getGrid();

                grid.randomizeTileStates(new Random(), simulator.getRuleSet());

                simulator.setGrid(grid);
            }

            updateViewPane(null);
        });
    }

    private CompletableFuture<Boolean> onIterationComplete(IterationResult iterationResult) {
        return FutureUtil.futureTaskJFX(() -> {
            Simulator simulator = RuleSeeker.getInstance().getSimulator();

            updateViewPane(simulator.getGrid());
        })
        .thenApplyAsync(v -> {
            while (true) {
                boolean loadedResumed;
                double loadedIntervalSeconds;

                synchronized (simulationLock) {
                    loadedResumed = this.resumed;
                    loadedIntervalSeconds = this.intervalSeconds;
                }

                if (!loadedResumed) {
                    return false;
                }

                Instant now = Instant.now();
                Duration durationElapsed = Duration.between(iterationResult.getComputationStart(), now);
                Duration requiredDuration = TimeUtil.ofSeconds(loadedIntervalSeconds);

                if (durationElapsed.compareTo(requiredDuration) > 0) {
                    return true;
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, FutureUtil.getBackgroundExecutor())
        .thenApplyAsync(cont -> {
            synchronized (simulationLock) {
                return this.resumeTaskRunning = cont && this.resumed;
            }
        }, FutureUtil.getJFXExecutor());
    }

    private Paint getCellColor(int state) {
        if (state == 0) {
            return Color.WHITE;
        }

        RuleSet ruleSet = RuleSeeker.getInstance().getSimulator().getRuleSet();
        int remainingStates = ruleSet.getNumberOfStates() - 1;
        state = state % remainingStates;

        return Color.hsb(360.0 * ((state / (double) remainingStates + ruleSet.getHueOffset()) % 1.0), 0.8, 1.0);
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

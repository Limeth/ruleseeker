package cz.cvut.fel.hlusijak.simulator;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.util.FutureUtil;
import cz.cvut.fel.hlusijak.util.JFXUtil;
import cz.cvut.fel.hlusijak.util.SerializationUtil;
import cz.cvut.fel.hlusijak.util.TimeUtil;
import cz.cvut.fel.hlusijak.util.Vector2d;
import cz.cvut.fel.hlusijak.util.Wrapper;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class SimulatorController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatorController.class);

    private final Stage stage;
    @FXML private Pane viewPane;

    // ToolBar
    @FXML private ToolBar toolbar;
    @FXML private Button loadButton;
    @FXML private Button saveButton;
    @FXML private Button settingsButton;

    // Simulation tab
    @FXML private Button resumeButton;
    @FXML private Button stepButton;
    @FXML private Button pauseButton;
    @FXML private TextField intervalTextField;
    @FXML private Slider intervalSlider;
    @FXML private ComboBox<Byte> editModeComboBox;
    @FXML private Button fillButton;
    @FXML private Button randomizeButton;

    // Synchronizes access to the following fields.
    private final Object simulationLock = new Object();
    private boolean resumed;
    private boolean resumeTaskRunning;
    private boolean ignoreIntervalEvents;
    private double intervalSeconds = 1.0;

    private CellShape[] cellShapes;
    private boolean mouseHeld = false;

    public SimulatorController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeView();
        initializeToolbar();
        initializeSidePane();
    }

    private void initializeView() {
        viewPane.layoutBoundsProperty()
                .addListener(observable -> updateViewPane(null, false));

        // Workaround for the previous listener not being triggered in the first frame
        Wrapper<InvalidationListener> firstRenderListener = new Wrapper<>(null);
        firstRenderListener.value = observable -> {
            observable.removeListener(firstRenderListener.value);
            updateViewPane(null, true);
        };
        viewPane.heightProperty().addListener(firstRenderListener.value);

        viewPane.setOnMousePressed(event -> mouseHeld = true);
        viewPane.setOnMouseReleased(event -> mouseHeld = false);
    }

    private void initializeToolbar() {
        loadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Open Simulation File");
            fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("RuleSeeker Simulation File", "*.rssim"),
                new ExtensionFilter("All Files", "*.*")
            );

            Optional<Path> pathOptional = Optional.ofNullable(fileChooser.showOpenDialog(stage)).map(File::toPath);

            if (!pathOptional.isPresent()) {
                return;
            }

            Path path = pathOptional.get();
            Kryo kryo = SerializationUtil.constructKryo();
            Input input = null;
            Simulator simulator;

            try {
                input = new Input(Files.newInputStream(path));
                simulator = kryo.readObject(input, Simulator.class);
            } catch (IOException e) {
                LOGGER.error("An error occurred while loading the simulation.", e);
                return;
            } finally {
                if (input != null) {
                    input.close();
                }
            }

            RuleSeeker.getInstance().setSimulator(simulator);
            updateViewPane(null, true);
            initializeEditModeComboBox();
        });
        saveButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Save Simulation File");
            fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("RuleSeeker Simulation File", "*.rssim"),
                new ExtensionFilter("All Files", "*.*")
            );

            Optional<Path> pathOptional = Optional.ofNullable(fileChooser.showSaveDialog(stage)).map(File::toPath);

            if (!pathOptional.isPresent()) {
                return;
            }

            Path path = pathOptional.get();

            if (!SerializationUtil.getExtension(path).isPresent()) {
                path = path.resolveSibling(path.getFileName().toString() + ".rssim");
            }

            Kryo kryo = SerializationUtil.constructKryo();
            Output output = null;

            try {
                output = new Output(Files.newOutputStream(path));
                kryo.writeObject(output, RuleSeeker.getInstance().getSimulator());
            } catch (IOException e) {
                LOGGER.error("An error occurred while saving the simulation.", e);
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        });
        settingsButton.setOnAction(event -> {
            Simulator simulator = SettingsDialog.open();

            RuleSeeker.getInstance().setSimulator(simulator);
            initializeEditModeComboBox();
            updateViewPane(null, true);
        });
    }

    private void initializeSidePane() {
        resumeButton.setOnAction(event -> {
            synchronized (simulationLock) {
                resumed = true;

                if (resumeTaskRunning) {
                    return;
                }

                Simulator simulator = RuleSeeker.getInstance().getSimulator();

                resumeTaskRunning = true;
                toolbar.setDisable(true);
                simulator.runAsync(this::onIterationComplete);
            }
        });

        stepButton.setOnAction(event -> {
            Simulator simulator = RuleSeeker.getInstance().getSimulator();

            simulator.nextIterationAsync().thenAcceptAsync(iterationResult ->
                updateViewPane(iterationResult.getNextGrid(), false), FutureUtil.getJFXExecutor());
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
        initializeEditModeComboBox();

        fillButton.setOnAction(event -> {
            Simulator simulator = RuleSeeker.getInstance().getSimulator();

            synchronized (simulator) {
                Grid grid = simulator.getGrid();

                grid.fillTileStates(getSelectedState());
                Arrays.stream(this.cellShapes).forEach(CellShape::updateColor);
            }
        });

        randomizeButton.setOnAction(event -> {
            Simulator simulator = RuleSeeker.getInstance().getSimulator();

            synchronized (simulator) {
                simulator.getGrid().randomizeTileStates(new Random(), simulator.getRuleSet());
            }

            updateViewPane(null, false);
        });
    }

    private void initializeEditModeComboBox() {
        JFXUtil.buildStateComboBox(editModeComboBox, () -> RuleSeeker.getInstance().getSimulator());
        editModeComboBox.getSelectionModel().selectFirst();
    }

    private CompletableFuture<Boolean> onIterationComplete(IterationResult iterationResult) {
        return FutureUtil.futureTaskJFX(() -> updateViewPane(iterationResult.getNextGrid(), false))
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
                    this.resumeTaskRunning = cont && this.resumed;

                    if (!this.resumeTaskRunning) {
                        this.toolbar.setDisable(false);
                    }

                    return this.resumeTaskRunning;
                }
            }, FutureUtil.getJFXExecutor());
    }

    private Pair<Double, Vector2d> createTransformation(Grid grid) {
        Vector2d boundingBox = grid.getGeometry().getVertexBoundingBox();
        Vector2d paneSize = Vector2d.of(viewPane.getWidth(), viewPane.getHeight());
        Vector2d ratio = paneSize.div(boundingBox);
        double scale = ratio.min();
        Vector2d scaledBoundingBox = boundingBox.mul(scale);
        Vector2d offset = paneSize.sub(scaledBoundingBox).div(2);

        return Pair.with(scale, offset);
    }

    public void updateViewPane(Grid grid, boolean gridShapeChanged) {
        final Grid finalGrid;

        if (grid == null) {
            Simulator simulator = RuleSeeker.getInstance().getSimulator();
            finalGrid = simulator.getGrid().clone();
        } else {
            finalGrid = grid;
        }

        GridGeometry gridGeometry = finalGrid.getGeometry();
        Pair<Double, Vector2d> transformation = createTransformation(finalGrid);
        double scale = transformation.getValue0();
        Vector2d offset = transformation.getValue1();

        if (gridShapeChanged) {
            viewPane.getChildren().clear();

            this.cellShapes = gridGeometry.tileIndexStream().boxed().map(tileIndex -> {
                byte state = finalGrid.getTileState(tileIndex);
                CellShape cellShape = new CellShape(this, tileIndex, state, Arrays.stream(gridGeometry.getTileVertices(tileIndex))
                        .map(vertex -> vertex.mul(scale))
                        .map(offset::add).toArray(Vector2d[]::new));

                viewPane.getChildren().add(cellShape);

                return cellShape;
            }).toArray(CellShape[]::new);
        } else if (this.cellShapes != null) {
            for (int i = 0; i < this.cellShapes.length; i++) {
                CellShape cellShape = this.cellShapes[i];

                cellShape.updateShape(Arrays.stream(gridGeometry.getTileVertices(i))
                        .map(vertex -> vertex.mul(scale))
                        .map(offset::add).toArray(Vector2d[]::new));
                cellShape.updateColor();
            }
        }
    }

    public byte getSelectedState() {
        return editModeComboBox.getValue();
    }

    public boolean isMouseHeld() {
        return mouseHeld;
    }
}

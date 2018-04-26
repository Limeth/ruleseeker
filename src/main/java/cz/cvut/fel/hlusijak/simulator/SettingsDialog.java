  package cz.cvut.fel.hlusijak.simulator;

import com.google.common.base.Preconditions;
import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.HexagonGridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.SquareGridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.TriangleGridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.EdgeSumRuleSet;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.simulator.ruleset.SumRuleSet;
import cz.cvut.fel.hlusijak.simulator.ruleset.VertexSumRuleSet;
import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.CustomStateColoringMethod;
import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.HueStateColoringMethod;
import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.StateColoringMethod;
import cz.cvut.fel.hlusijak.util.JFXUtil;
import cz.cvut.fel.hlusijak.util.Vector2i;
import cz.cvut.fel.hlusijak.util.Wrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.scene.control.ButtonBar.ButtonData;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SettingsDialog extends Alert implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsDialog.class);
    private final Simulator simulator;

    @FXML private TabPane tabPane;

    // Grid tab {{{
    @FXML private Tab gridTab;
    @FXML private ChoiceBox<GridGeometryItem<?>> gridGeometryChoiceBox;
    @FXML private Spinner<Integer> gridWidthSpinner;
    @FXML private Spinner<Integer> gridHeightSpinner;
    // }}}

    // Rule Set tab {{{
    @FXML private Tab ruleSetTab;
    @FXML private ChoiceBox<RuleSetItem<?>> ruleSetTypeChoiceBox;
    @FXML private Spinner<Integer> ruleSetCellStatesSpinner;
    @FXML private Button ruleSetResetRulesButton;
    @FXML private Button ruleSetRandomizeRulesButton;
    @FXML private ScrollPane ruleSetScrollPane;
    // }}}

    // State Colors tab {{{
    @FXML private Tab stateColorsTab;
    @FXML private ChoiceBox<ColoringMethodItem<?>> stateColorsMethodChoiceBox;
    @FXML private HBox stateColorsPreviewHBox;
    @FXML private ScrollPane stateColorsScrollPane;
    // }}}

    private boolean ruleViewInvalidated = true;
    private boolean stateColorsInvalidated = true;

    private SettingsDialog(Simulator simulator) {
        super(AlertType.NONE);
        Preconditions.checkNotNull(simulator);

        this.simulator = simulator;
    }

    public static Simulator open() {
        Simulator previousSimulator = RuleSeeker.getInstance().getSimulator();

        Preconditions.checkNotNull(previousSimulator, "The simulator must be set.");

        final Simulator simulator = previousSimulator.clone();
        SettingsDialog dialog = new SettingsDialog(simulator);

        try {
            FXMLLoader loader = new FXMLLoader(SettingsDialog.class.getResource("template_dialog.fxml"));
            loader.setController(dialog);
            DialogPane dialogPane = loader.load();
            dialog.setDialogPane(dialogPane);
        } catch (IOException e) {
            LOGGER.error("An exception occurred while opening the settings dialog.", e);
        }

        Wrapper<Simulator> nextSimulator = new Wrapper(null);

        dialog.setResizable(true);
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType.getButtonData().equals(ButtonData.FINISH)) {
                dialog.updateSimulator();

                nextSimulator.value = dialog.simulator;
            } else {
                nextSimulator.value = previousSimulator;
            }
        });

        return nextSimulator.value;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateSimulator();

            if ((newValue == ruleSetTab) && ruleViewInvalidated) {
                ruleViewInvalidated = false;

                renderRuleView();
            } else if ((newValue == stateColorsTab) && stateColorsInvalidated) {
                stateColorsInvalidated = false;

                renderStateColors();
            }
        });

        initializeGridTab();
        initializeRuleSetTab();
        initializeStateColorsTab();
    }

    private void initializeGridTab() {
        gridGeometryChoiceBox.getItems().addAll(
            new GridGeometryItem<TriangleGridGeometry>(TriangleGridGeometry.class, "Triangular", 2, 2, TriangleGridGeometry::new),
            new GridGeometryItem<SquareGridGeometry>(SquareGridGeometry.class, "Rectangular", 1, 1, SquareGridGeometry::new),
            new GridGeometryItem<HexagonGridGeometry>(HexagonGridGeometry.class, "Hexagonal", 1, 2, HexagonGridGeometry::new)
        );
        gridWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
        gridHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));

        // Assign defaults
        Item.selectByClass(gridGeometryChoiceBox, simulator.getGrid().getGeometry().getClass());
        Vector2i gridDimensions = simulator.getGrid().getGeometry().getDimensions();

        gridWidthSpinner.getValueFactory().setValue(gridDimensions.getX());
        gridHeightSpinner.getValueFactory().setValue(gridDimensions.getY());
        updateGridSizeRequirements();

        // Listeners
        gridGeometryChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateGridSizeRequirements();
        });
        JFXUtil.fixUnfocus(gridWidthSpinner);
        JFXUtil.fixUnfocus(gridHeightSpinner);
        gridWidthSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            roundIntegerSpinnerValue(gridWidthSpinner);
        });
        gridHeightSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            roundIntegerSpinnerValue(gridHeightSpinner);
        });
    }

    private void initializeRuleSetTab() {
        ruleSetTypeChoiceBox.getItems().addAll(
            new RuleSetItem(EdgeSumRuleSet.class, "Edge Combination (Von Neumann's generalized)", () -> {
                return new EdgeSumRuleSet(simulator.getGrid().getGeometry(), ruleSetCellStatesSpinner.getValue());
            }),
            new RuleSetItem(VertexSumRuleSet.class, "Vertex Combination (Moore's generalized)", () -> {
                return new VertexSumRuleSet(simulator.getGrid().getGeometry(), ruleSetCellStatesSpinner.getValue());
            })
        );
        ruleSetCellStatesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, Integer.MAX_VALUE));
        JFXUtil.fixUnfocus(ruleSetCellStatesSpinner);

        // Assign defaults
        Item.selectByClass(ruleSetTypeChoiceBox, simulator.getRuleSet().getClass());
        ruleSetCellStatesSpinner.getValueFactory().setValue(simulator.getRuleSet().getNumberOfStates());
        renderRuleView();

        // Listeners
        ruleSetTypeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                updateRuleSet();
            }
        });
        ruleSetCellStatesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue < 2) {
                return;
            }

            if (!oldValue.equals(newValue)) {
                int[] rules = simulator.getRuleSet().getRules();

                for (int ruleIndex = 0; ruleIndex < rules.length; ruleIndex++) {
                    if (rules[ruleIndex] >= newValue) {
                        rules[ruleIndex] = 0;
                    }
                }

                simulator.getStateColoringMethod().clearCache();
                updateRuleSet();

                stateColorsInvalidated = true;
            }
        });
        ruleSetResetRulesButton.setOnAction(event -> {
            Arrays.fill(simulator.getRuleSet().getRules(), 0);
            renderRuleView();
        });
        ruleSetRandomizeRulesButton.setOnAction(event -> {
            simulator.getRuleSet().randomizeRules(new Random());
            renderRuleView();
        });
    }

    private void initializeStateColorsTab() {
        stateColorsMethodChoiceBox.getItems().addAll(
            new ColoringMethodItem<HueStateColoringMethod>(HueStateColoringMethod.class, "Evenly spaced hue values") {
                @Override
                protected HueStateColoringMethod construct(List<Paint> previousColors) {
                    return HueStateColoringMethod.random();
                }

                @Override
                protected Node buildControls(HueStateColoringMethod method) {
                    GridPane root = new GridPane();
                    Slider hueOffsetSlider = new Slider(0, 1, method.getHueOffset());

                    JFXUtil.applyGridPaneStyle(root, true);
                    root.addRow(0, new Label("Hue offset"), hueOffsetSlider);

                    hueOffsetSlider.setId("stateColorsHueOffset");
                    hueOffsetSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                        method.setHueOffset(newValue.doubleValue());
                        method.clearCache();
                        renderStateColorsPreview();

                        ruleViewInvalidated = true;
                    });

                    return root;
                }
            },
            new ColoringMethodItem<CustomStateColoringMethod>(CustomStateColoringMethod.class, "Custom colors") {
                @Override
                protected CustomStateColoringMethod construct(List<Paint> previousColors) {
                    return new CustomStateColoringMethod(previousColors);
                }

                @Override
                protected Node buildControls(CustomStateColoringMethod method) {
                    GridPane root = new GridPane();
                    List<Paint> paints = method.getColors();
                    List<Color> colors = paints.stream().map(paint -> {
                        if (paint instanceof Color) {
                            return (Color) paint;
                        } else {
                            return Color.WHITE;
                        }
                    }).collect(Collectors.toList());

                    JFXUtil.applyGridPaneStyle(root, true);
                    IntStream.range(0, colors.size()).forEach(colorIndex -> {
                        ColorPicker colorPicker = new ColorPicker();

                        colorPicker.setValue(colors.get(colorIndex));
                        root.add(colorPicker, 0, colorIndex, 2, 1);
                        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                            method.setColor(colorIndex, newValue);
                            renderStateColorsPreview();

                            ruleViewInvalidated = true;
                        });
                    });

                    Button addButton = new Button("Add");
                    Button removeButton = new Button("Remove");

                    addButton.setOnAction(event -> {
                        method.addColor(Color.WHITE);
                        renderStateColors();

                        ruleViewInvalidated = true;
                    });

                    if (method.size() <= 2) {
                        removeButton.setDisable(true);
                    } else {
                        removeButton.setOnAction(event -> {
                            method.removeLastColor();
                            renderStateColors();

                            ruleViewInvalidated = true;
                        });
                    }

                    root.addRow(colors.size(), addButton, removeButton);

                    return root;
                }
            }
        );

        // Assign defaults
        renderStateColors();

        // Listeners
        stateColorsMethodChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            List<Paint> previousColors = simulator.getStateColoringMethod().getColors(simulator.getRuleSet());
            StateColoringMethod stateColoringMethod = newValue.construct(previousColors);

            simulator.setStateColoringMethod(stateColoringMethod);
            renderStateColors();

            ruleViewInvalidated = true;
        });
    }

    private void renderStateColors() {
        StateColoringMethod currentStateColoringMethod = simulator.getStateColoringMethod();
        ColoringMethodItem<StateColoringMethod> item = /* topkek */ (ColoringMethodItem<StateColoringMethod>) (Object) Item.selectByClass(stateColorsMethodChoiceBox, currentStateColoringMethod.getClass());
        Node root = item.buildControls(currentStateColoringMethod);

        stateColorsScrollPane.setContent(root);
        renderStateColorsPreview();
    }

    private void renderStateColorsPreview() {
        stateColorsPreviewHBox.getChildren().clear();
        simulator.getRuleSet().stateStream().forEach(state -> {
            stateColorsPreviewHBox.getChildren().add(buildStateNode(state));
        });
    }

    private void updateRuleSet() {
        RuleSet previousRuleSet = simulator.getRuleSet();
        int[] previousRules = previousRuleSet.getRules();
        RuleSet nextRuleSet = ruleSetTypeChoiceBox.getValue().constructor.get();
        int[] nextRules = nextRuleSet.getRules();

        System.arraycopy(previousRules, 0, nextRules, 0, Math.min(previousRules.length, nextRules.length));

        simulator.setRuleSet(nextRuleSet);
        renderRuleView();
    }

    private void renderRuleView() {
        SumRuleSet<?> ruleSet = (SumRuleSet) simulator.getRuleSet();
        int neighbourhoodSize = ruleSet.getNeighbourhoodSize();
        final GridPane grid = new GridPane();

        JFXUtil.applyGridPaneStyle(grid, false);
        grid.add(new Label("Previous"), 0, 0);
        grid.add(new Label("Neighbours"), 1, 0, neighbourhoodSize, 1);
        grid.add(new Label("Next"), neighbourhoodSize + 1, 0);
        ruleSet.enumerateRules()
            .forEach(rule -> {
                Node[] row = new Node[neighbourhoodSize + 2];
                row[0] = buildStateNode(rule.getPreviousState());
                int[] stateCount = rule.getStateCount();
                int state = 0;

                for (int neighbourIndex = 0; neighbourIndex < neighbourhoodSize; neighbourIndex++) {
                    while (stateCount[state] <= 0) {
                        state++;
                    }

                    row[neighbourIndex + 1] = buildStateNode(state);
                    stateCount[state]--;
                }

                ComboBox<Integer> nextStateComboBox = new ComboBox<>();

                ruleSet.stateStream().forEach(nextStateComboBox.getItems()::add);

                ComboBox<Integer> stateComboBox = JFXUtil.buildStateComboBox(null, () -> simulator);

                stateComboBox.getSelectionModel().select(rule.getNextState());
                stateComboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    ruleSet.getRules()[rule.getIndex()] = newValue.intValue();
                });

                row[row.length - 1] = stateComboBox;

                grid.addRow(rule.getIndex() + 1, row);
            });
        ruleSetScrollPane.setContent(grid);
    }

    private Node buildStateNode(int state) {
        final int POLYGON_SIZE = 20;
        Polygon polygon = new Polygon(0, 0, POLYGON_SIZE, 0, POLYGON_SIZE, POLYGON_SIZE, 0, POLYGON_SIZE);
        List<Paint> colors = simulator.getStateColoringMethod().getColors(simulator.getRuleSet());

        polygon.setFill(colors.get(state));
        polygon.setLayoutX(0);
        polygon.setLayoutY(0);
        polygon.setStrokeType(StrokeType.CENTERED);
        polygon.setStroke(Color.BLACK);

        return polygon;
    }

    private void roundIntegerSpinnerValue(Spinner<Integer> spinner) {
        int step = ((SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory()).getAmountToStepBy();
        int prevValue = spinner.getValue();
        int nextValue = step * (int) Math.ceil((double) prevValue / (double) step);

        if (prevValue != nextValue) {
            spinner.getValueFactory().setValue(nextValue);
        }
    }

    private void updateSpinnerValueFactory(Spinner<Integer> spinner, int step) {
        ((SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory()).setAmountToStepBy(step);
        ((SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory()).setMin(step);
        roundIntegerSpinnerValue(spinner);
    }

    private void updateGridSizeRequirements() {
        GridGeometryItem<?> item = gridGeometryChoiceBox.getValue();
        updateSpinnerValueFactory(gridWidthSpinner, item.widthSteps);
        updateSpinnerValueFactory(gridHeightSpinner, item.heightSteps);
    }

    private void updateSimulator() {
        Grid grid = this.simulator.getGrid();
        GridGeometry gridGeometry = grid.getGeometry();
        GridGeometryItem<?> chosenGridGeometryItem = this.gridGeometryChoiceBox.getValue();
        Vector2i chosenGridDimensions = Vector2i.of(this.gridWidthSpinner.getValue(), this.gridHeightSpinner.getValue());
        boolean gridAltered = !chosenGridGeometryItem.value.isAssignableFrom(gridGeometry.getClass())
            || !chosenGridDimensions.equals(gridGeometry.getDimensions());

        if (gridAltered) {
            gridGeometry = chosenGridGeometryItem.constructor.apply(chosenGridDimensions);
            grid = new Grid(gridGeometry);

            this.simulator.setGrid(grid);
            updateRuleSet();
        }
    }

    private static class Item<T> {
        final T value;
        final String displayName;

        private Item(T value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        private static <T> Item<Class<? extends T>> selectByClass(ChoiceBox<? extends Item<Class<? extends T>>> choiceBox, Class<? extends T> clazz) {
            List<? extends Item<Class<? extends T>>> items = choiceBox.getItems();

            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                Item<Class<? extends T>> item = items.get(itemIndex);

                if (item.value.isAssignableFrom(clazz)) {
                    choiceBox.getSelectionModel().select(itemIndex);
                    return item;
                }
            }

            throw new IllegalStateException("No item found for class " + clazz);
        }
    }

    private static class GridGeometryItem<T extends GridGeometry> extends Item<Class<T>> {
        final int widthSteps;
        final int heightSteps;
        final Function<Vector2i, T> constructor;

        private GridGeometryItem(Class<T> value, String displayName, int widthSteps, int heightSteps, Function<Vector2i, T> constructor) {
            super(value, displayName);

            this.widthSteps = widthSteps;
            this.heightSteps = heightSteps;
            this.constructor = constructor;
        }
    }

    private static class RuleSetItem<T extends RuleSet> extends Item<Class<T>> {
        final Supplier<T> constructor;

        private RuleSetItem(Class<T> value, String displayName, Supplier<T> constructor) {
            super(value, displayName);

            this.constructor = constructor;
        }
    }

    private static abstract class ColoringMethodItem<T extends StateColoringMethod> extends Item<Class<T>> {
        private ColoringMethodItem(Class<T> value, String displayName) {
            super(value, displayName);
        }

        protected abstract T construct(List<Paint> previousColors);
        protected abstract Node buildControls(T method);

        private Pair<T, Node> constructAndBuildControls(List<Paint> previousColors) {
            T stateColoringMethod = construct(previousColors);

            return Pair.with(stateColoringMethod, buildControls(stateColoringMethod));
        }
    }
}

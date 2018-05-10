  package cz.cvut.fel.hlusijak.simulator;

  import com.google.common.base.Preconditions;
  import cz.cvut.fel.hlusijak.RuleSeeker;
  import cz.cvut.fel.hlusijak.simulator.grid.Grid;
  import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
  import cz.cvut.fel.hlusijak.simulator.grid.geometry.HexagonGridGeometry;
  import cz.cvut.fel.hlusijak.simulator.grid.geometry.SquareGridGeometry;
  import cz.cvut.fel.hlusijak.simulator.grid.geometry.TriangleGridGeometry;
  import cz.cvut.fel.hlusijak.simulator.ruleset.EdgeSumRuleSetType;
  import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
  import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSetType;
  import cz.cvut.fel.hlusijak.simulator.ruleset.SumRuleSetType;
  import cz.cvut.fel.hlusijak.simulator.ruleset.VertexSumRuleSetType;
  import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.CustomStateColoringMethod;
  import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.HueStateColoringMethod;
  import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.StateColoringMethod;
  import cz.cvut.fel.hlusijak.util.JFXUtil;
  import cz.cvut.fel.hlusijak.util.Vector2i;
  import cz.cvut.fel.hlusijak.util.Wrapper;
  import javafx.fxml.FXML;
  import javafx.fxml.FXMLLoader;
  import javafx.fxml.Initializable;
  import javafx.geometry.HPos;
  import javafx.geometry.Insets;
  import javafx.geometry.Pos;
  import javafx.scene.Node;
  import javafx.scene.control.*;
  import javafx.scene.control.ButtonBar.ButtonData;
  import javafx.scene.layout.GridPane;
  import javafx.scene.layout.HBox;
  import javafx.scene.layout.Priority;
  import javafx.scene.paint.Color;
  import javafx.scene.paint.Paint;
  import javafx.scene.shape.Polygon;
  import javafx.scene.shape.StrokeType;
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
    @FXML private Spinner<Integer> ruleSetCellStatesSpinner; // Int because there's no ByteSpinnerValueFactory
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
            new GridGeometryItem<>(TriangleGridGeometry.class, "Triangular", 2, 2, TriangleGridGeometry::new),
            new GridGeometryItem<>(SquareGridGeometry.class, "Rectangular", 1, 1, SquareGridGeometry::new),
            new GridGeometryItem<>(HexagonGridGeometry.class, "Hexagonal", 1, 2, HexagonGridGeometry::new)
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
            new RuleSetItem(EdgeSumRuleSetType.class, "Edge Combination (Von Neumann's generalized)", () -> {
                return new EdgeSumRuleSetType(simulator.getGrid().getGeometry(), (byte) (int) ruleSetCellStatesSpinner.getValue());
            }),
            new RuleSetItem(VertexSumRuleSetType.class, "Vertex Combination (Moore's generalized)", () -> {
                return new VertexSumRuleSetType(simulator.getGrid().getGeometry(), (byte) (int) ruleSetCellStatesSpinner.getValue());
            })
        );
        ruleSetCellStatesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, Integer.MAX_VALUE));
        JFXUtil.fixUnfocus(ruleSetCellStatesSpinner);

        // Assign defaults
        Item.selectByClass(ruleSetTypeChoiceBox, simulator.getRuleSet().getType().getClass());
        ruleSetCellStatesSpinner.getValueFactory().setValue((int) simulator.getRuleSet().getType().getNumberOfStates());
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
                byte[] rules = simulator.getRuleSet().getRules();

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
            Arrays.fill(simulator.getRuleSet().getRules(), (byte) 0);
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

                    root.setMaxWidth(Double.MAX_VALUE);
                    root.setAlignment(Pos.TOP_CENTER);
                    hueOffsetSlider.setMaxWidth(Double.MAX_VALUE);
                    GridPane.setFillWidth(hueOffsetSlider, true);
                    GridPane.setHgrow(hueOffsetSlider, Priority.ALWAYS);
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
                    root.setAlignment(Pos.TOP_CENTER);
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

                    addButton.setMaxWidth(Double.MAX_VALUE);
                    removeButton.setMaxWidth(Double.MAX_VALUE);

                    GridPane.setFillWidth(addButton, true);
                    GridPane.setFillWidth(removeButton, true);

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

        stateColorsScrollPane.setFitToWidth(true);
        stateColorsScrollPane.setContent(root);
        renderStateColorsPreview();
    }

    private void renderStateColorsPreview() {
        stateColorsPreviewHBox.getChildren().clear();
        simulator.getRuleSet().getType().stateStream().forEach(state -> {
            stateColorsPreviewHBox.getChildren().add(buildStateNode(state));
        });
    }

    private void updateRuleSet() {
        RuleSet previousRuleSet = simulator.getRuleSet();
        byte[] previousRules = previousRuleSet.getRules();
        RuleSetType nextRuleSetType = ruleSetTypeChoiceBox.getValue().constructor.get();
        RuleSet nextRuleSet = new RuleSet(nextRuleSetType);
        byte[] nextRules = nextRuleSet.getRules();

        System.arraycopy(previousRules, 0, nextRules, 0, Math.min(previousRules.length, nextRules.length));
        nextRuleSet.setRules(nextRules);
        simulator.setRuleSet(nextRuleSet);
        renderRuleView();
    }

    public static final Vector2i SPACING = Vector2i.of(16, 12);

    private Node styleCell(Node node, boolean middle, boolean firstRow) {
        GridPane.setMargin(node, new Insets(0, middle ? SPACING.getX() : 0, firstRow ? SPACING.getY() : 0, middle ? SPACING.getX() : 0));
        GridPane.setHalignment(node, HPos.CENTER);
        return node;
    }

    private void renderRuleView() {
        RuleSet ruleSet = simulator.getRuleSet();
        SumRuleSetType<?> ruleSetType = (SumRuleSetType) ruleSet.getType();
        int neighbourhoodSize = ruleSetType.getNeighbourhoodSize();
        final GridPane grid = new GridPane();

        JFXUtil.applyGridPaneStyle(grid, false);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.add(styleCell(new Label("Previous"), false, true), 0, 0);
        grid.add(styleCell(new Label("Neighbours"), true, true), 1, 0);
        grid.add(styleCell(new Label("Next"), false, true), 2, 0);
        ruleSetType.enumerateRules(ruleSet)
            .forEach(rule -> {
                Node[] row = new Node[3];
                // First column
                row[0] = styleCell(buildStateNode(rule.getPreviousState()), false, false);

                // Neighbourhood
                HBox neighbourhoodBox = new HBox();
                int[] stateCount = rule.getStateCount();
                byte state = 0;

                for (int neighbourIndex = 0; neighbourIndex < neighbourhoodSize; neighbourIndex++) {
                    while (stateCount[state] <= 0) {
                        state++;
                    }

                    neighbourhoodBox.getChildren().add(buildStateNode(state));
                    stateCount[state]--;
                }

                row[1] = styleCell(neighbourhoodBox, true, false);

                // Last column
                ComboBox<Byte> nextStateComboBox = new ComboBox<>();

                ruleSet.getType().stateStream().forEach(nextStateComboBox.getItems()::add);

                ComboBox<Byte> stateComboBox = JFXUtil.buildStateComboBox(null, () -> simulator);

                stateComboBox.getSelectionModel().select(rule.getNextState());
                stateComboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    ruleSet.setRule(rule.getIndex(), newValue.byteValue());
                });

                row[2] = styleCell(stateComboBox, false, false);

                grid.addRow(rule.getIndex() + 1, row);
            });
        ruleSetScrollPane.setContent(grid);
    }

    private Node buildStateNode(byte state) {
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
        }

        updateRuleSet();
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

    private static class RuleSetItem<T extends RuleSetType> extends Item<Class<T>> {
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

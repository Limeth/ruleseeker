package cz.cvut.fel.hlusijak.simulator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.google.common.base.Preconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.HexagonGridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.SquareGridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.TriangleGridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

public class SettingsDialog<StateColoringMethod> extends Alert implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsDialog.class);
    private final Simulator simulator;

    @FXML private TabPane tabPane;

    // Grid tab {{{
    @FXML private ChoiceBox<GridGeometryItem<?>> gridGeometryChoiceBox;
    @FXML private Spinner<Integer> gridWidthSpinner;
    @FXML private Spinner<Integer> gridHeightSpinner;
    @FXML private Spinner<Integer> gridCellStatesSpinner;
    // }}}

    // Rule Set tab {{{
    @FXML private ChoiceBox<Item<? extends RuleSet>> ruleSetTypeChoiceBox;
    @FXML private Button ruleSetResetRulesButton;
    @FXML private Button ruleSetRandomizeRulesButton;
    @FXML private VBox ruleSetVBox;
    // }}}

    // State Colors tab {{{
    @FXML private ChoiceBox<Item<? extends StateColoringMethod>> stateColorsMethodChoiceBox;
    @FXML private VBox stateColorsVBox;
    // }}}

    private SettingsDialog(Simulator simulator) {
        super(AlertType.NONE);
        Preconditions.checkNotNull(simulator);

        this.simulator = simulator;
    }

    public static SettingsDialog open() {
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

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.FINISH) {
                RuleSeeker.getInstance().setSimulator(simulator);
            }
        });

        return dialog;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateSimulator();
        });

        initializeGridTab();
        initializeRuleSetTab();
        // initializeStateColorsTab();
    }

    private void initializeGridTab() {
        gridGeometryChoiceBox.getItems().addAll(
            new GridGeometryItem(TriangleGridGeometry.class, "Triangular", 2, 2),
            new GridGeometryItem(SquareGridGeometry.class, "Rectangular", 1, 1),
            new GridGeometryItem(HexagonGridGeometry.class, "Hexagonal", 1, 2)
        );
        gridWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
        gridWidthSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                roundIntegerSpinnerValue(gridWidthSpinner);
            }
        });
        gridHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
        gridHeightSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                roundIntegerSpinnerValue(gridHeightSpinner);
            }
        });
        gridCellStatesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, Integer.MAX_VALUE));

        // Assign defaults
        gridGeometryChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateGridSizeRequirements();
        });
        gridGeometryChoiceBox.getSelectionModel().selectFirst();
    }

    private void initializeRuleSetTab() {

    }

    private void roundIntegerSpinnerValue(Spinner<Integer> spinner) {
        int step = ((SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory()).getAmountToStepBy();
        int prevValue = spinner.getValue();

        spinner.getValueFactory().setValue(0);
        spinner.getValueFactory().setValue(step * (int) Math.ceil((double) prevValue / (double) step));
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
        // TODO
    }

    private static class Item<T> {
        private final T value;
        private final String displayName;

        private Item(T value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private static class GridGeometryItem<T extends GridGeometry> extends Item<Class<T>> {
        private final int widthSteps;
        private final int heightSteps;

        private GridGeometryItem(Class<T> value, String displayName, int widthSteps, int heightSteps) {
            super(value, displayName);

            this.widthSteps = widthSteps;
            this.heightSteps = heightSteps;
        }
    }

    private static class RuleSetItem<T extends RuleSet> extends Item<Class<T>> {
        private final int widthSteps;
        private final int heightSteps;

        private RuleSetItem(Class<T> value, String displayName, int widthSteps, int heightSteps) {
            super(value, displayName);

            this.widthSteps = widthSteps;
            this.heightSteps = heightSteps;
        }
    }
}

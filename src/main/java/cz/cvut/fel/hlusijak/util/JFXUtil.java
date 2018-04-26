package cz.cvut.fel.hlusijak.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cz.cvut.fel.hlusijak.simulator.Simulator;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public final class JFXUtil {
    private JFXUtil() {}

    public static void fixUnfocus(Spinner<?> spinner) {
        TextFormatter formatter = new TextFormatter(spinner.getValueFactory().getConverter(), spinner.getValueFactory().getValue());

        spinner.getEditor().setTextFormatter(formatter);
        spinner.getValueFactory().valueProperty().bindBidirectional(formatter.valueProperty());
    }

    // I don't even. JavaFX doesn't make registered listeners accessible.
    private static final Map<ObservableValue, List<ChangeListener>> LISTENERS = Maps.newIdentityHashMap();

    public static void clearRegisteredListeners(ObservableValue<?> observableValue) {
        Optional.ofNullable(LISTENERS.remove(observableValue))
            .ifPresent(listeners -> listeners.forEach(observableValue::removeListener));
    }

    public static <T> void addAndRegisterListener(ObservableValue<T> observableValue, ChangeListener<? super T> listener) {
        List<ChangeListener> listeners = LISTENERS.computeIfAbsent(observableValue, key -> Lists.newArrayList());

        listeners.add(listener);
        observableValue.addListener(listener);
    }

    public static ComboBox<Integer> buildStateComboBox(ComboBox<Integer> comboBoxArg, Simulator simulator) {
        final ComboBox<Integer> comboBox;

        if (comboBoxArg == null) {
            comboBox = new ComboBox<>();
        } else {
            comboBox = comboBoxArg;
        }

        comboBox.getSelectionModel().clearSelection();
        comboBox.getItems().clear();
        // comboBox.setCellFactory(listView -> new ListCell<Integer>() {
        //     @Override
        //     protected void updateItem(Integer item, boolean empty) {
        //         super.updateItem(item, empty);

        //         if (item == null || empty) {
        //             return;
        //         }

        //         List<Paint> stateColors = simulator.getStateColoringMethod().getColors(simulator.getRuleSet());

        //         setText(Integer.toString(item));
        //         setBackground(new Background(new BackgroundFill(stateColors.get(item), null, null)));
        //     }
        // });

        ChangeListener<Integer> listener = (observable, oldValue, newValue) -> {
            Paint paint;

            if (newValue != null) {
                List<Paint> stateColors = simulator.getStateColoringMethod().getColors(simulator.getRuleSet());
                paint = stateColors.get(newValue);
            } else {
                paint = Color.TRANSPARENT;
            }

            comboBox.setBackground(new Background(new BackgroundFill(paint, null, null)));
        };

        clearRegisteredListeners(comboBox.valueProperty());
        addAndRegisterListener(comboBox.valueProperty(), listener);
        comboBox.valueProperty().addListener(listener);

        simulator.getRuleSet().stateStream().forEach(comboBox.itemsProperty().get()::add);

        return comboBox;
    }

    public static void applyGridPaneStyle(GridPane gridPane, boolean applyGaps) {
        if (applyGaps) {
            gridPane.setHgap(16);
            gridPane.setVgap(12);
        }

        gridPane.setPadding(new Insets(12, 16, 12, 16));
    }
}

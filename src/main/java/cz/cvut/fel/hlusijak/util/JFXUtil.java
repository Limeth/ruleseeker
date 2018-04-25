package cz.cvut.fel.hlusijak.util;

import java.util.List;

import cz.cvut.fel.hlusijak.simulator.Simulator;
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

public final class JFXUtil {
    private JFXUtil() {}

    public static void fixUnfocus(Spinner<?> spinner) {
        TextFormatter formatter = new TextFormatter(spinner.getValueFactory().getConverter(), spinner.getValueFactory().getValue());

        spinner.getEditor().setTextFormatter(formatter);
        spinner.getValueFactory().valueProperty().bindBidirectional(formatter.valueProperty());
    }

    public static ComboBox<Integer> buildStateComboBox(ComboBox<Integer> comboBoxArg, Simulator simulator) {
        final ComboBox<Integer> comboBox;

        if (comboBoxArg == null) {
            comboBox = new ComboBox<>();
        } else {
            comboBox = comboBoxArg;
        }

        comboBox.setCellFactory(listView -> new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    return;
                }

                List<Paint> stateColors = simulator.getStateColoringMethod().getColors(simulator.getRuleSet());

                setText(Integer.toString(item));
                setBackground(new Background(new BackgroundFill(stateColors.get(item), null, null)));
            }
        });

        comboBox.valueProperty().addListener(((observable, oldValue, newValue) -> {
            List<Paint> stateColors = simulator.getStateColoringMethod().getColors(simulator.getRuleSet());

            comboBox.setBackground(new Background(new BackgroundFill(stateColors.get(newValue), null, null)));
        }));

        simulator.getRuleSet().stateStream().forEach(comboBox.itemsProperty().get()::add);

        return comboBox;
    }
}

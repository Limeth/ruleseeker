package cz.cvut.fel.hlusijak.simulator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SimulatorController implements Initializable {
    @FXML private GridPane viewGrid;
    @FXML private Button resumeButton;
    @FXML private Button stepButton;
    @FXML private Button pauseButton;
    @FXML private ChoiceBox editModeChoiceBox;
    @FXML private Button fillButton;
    @FXML private Button randomizeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

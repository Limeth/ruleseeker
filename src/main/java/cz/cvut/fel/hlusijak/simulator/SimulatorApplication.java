package cz.cvut.fel.hlusijak.simulator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimulatorApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("template.fxml"));
        primaryStage.setTitle("ruleseeker");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}

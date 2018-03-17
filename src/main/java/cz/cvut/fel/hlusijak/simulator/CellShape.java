package cz.cvut.fel.hlusijak.simulator;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;

public class CellShape extends Polygon {
    private Simulator simulator;
    private int index;

    public CellShape() {
        super();

        getPoints().addAll(-50.0, 40.0, 50.0, 40.0, 0.0, -60.0);
        fillProperty().setValue(Color.DODGERBLUE);
        setLayoutX(0);
        setLayoutY(0);
        setStrokeType(StrokeType.CENTERED);
        setStroke(Color.BLACK);
    }
}

package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.util.Vector2d;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;

public class CellShape extends Polygon {
    private Simulator simulator;
    private int index;

    public CellShape(Paint fill, Vector2d... vertices) {
        super();

        for (Vector2d vertex : vertices) {
            getPoints().addAll(vertex.getX(), vertex.getY());
        }

        //getPoints().addAll(-50.0, 40.0, 50.0, 40.0, 0.0, -60.0);
        fillProperty().setValue(fill);
        setLayoutX(0);
        setLayoutY(0);
        setStrokeType(StrokeType.CENTERED);
        setStroke(Color.BLACK);
    }
}

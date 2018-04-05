package cz.cvut.fel.hlusijak.simulator;

import com.sun.javafx.css.Rule;
import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.util.Vector2d;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;

public class CellShape extends Polygon {
    private Simulator simulator;
    private SimulatorController simulatorController;
    private int index;
    private int state;

    public CellShape(SimulatorController simulatorController, int state, Vector2d... vertices) {
        super();

        this.simulator = RuleSeeker.getInstance().getSimulator();
        this.simulatorController = simulatorController;

        for (Vector2d vertex : vertices) {
            getPoints().addAll(vertex.getX(), vertex.getY());
        }

        setLayoutX(0);
        setLayoutY(0);
        setStrokeType(StrokeType.CENTERED);
        setStroke(Color.BLACK);
        setOnMousePressed(this::onClick);
        setOnDragDetected(event -> {
            startFullDrag();
            this.onClick(event);
        });
        setOnMouseDragEntered(this::onClick);

        setState(state);
    }

    private void onClick(InputEvent event) {
        setState(simulatorController.getSelectedState());
        simulator.getGrid().setTileState(index, this.state);
    }

    public void setState(int state) {
        this.state = state;

        updateColor();
    }

    public void updateState() {
        setState(this.simulator.getGrid().getTileState(index));
    }

    public void updateColor() {
        fillProperty().setValue(simulatorController.getCellColor(this.state));
    }
}

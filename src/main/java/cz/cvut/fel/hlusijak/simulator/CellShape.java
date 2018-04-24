package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.util.Vector2d;
import javafx.scene.input.InputEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;

public class CellShape extends Polygon {
    private final Simulator simulator;
    private final SimulatorController simulatorController;
    private final int index;

    public CellShape(SimulatorController simulatorController, int index, int state, Vector2d... vertices) {
        super();

        this.simulator = RuleSeeker.getInstance().getSimulator();
        this.simulatorController = simulatorController;
        this.index = index;

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

        updateColor(state);
    }

    private void onClick(InputEvent event) {
        int state = simulatorController.getSelectedState();

        synchronized (this.simulator) {
            simulator.getGrid().setTileState(index, state);
        }

        updateColor(state);
    }

    public void updateColor(int state) {
        fillProperty().setValue(simulator.getStateColoringMethod().getColors(simulator.getRuleSet()).get(state));
    }

    public void updateColor() {
        synchronized (this.simulator) {
            updateColor(this.simulator.getGrid().getTileState(index));
        }
    }

    public int getTileIndex() {
        return index;
    }
}

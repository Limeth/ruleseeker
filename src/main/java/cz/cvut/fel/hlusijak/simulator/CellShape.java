package cz.cvut.fel.hlusijak.simulator;

import cz.cvut.fel.hlusijak.RuleSeeker;
import cz.cvut.fel.hlusijak.util.Vector2d;
import javafx.scene.input.InputEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;

/**
 * A node used to draw a grid cell of a simulation in a JavaFX application.
 */
public class CellShape extends Polygon {
    private final SimulatorController simulatorController;
    private final int index;

    /**
     * @param simulatorController The {@link SimulatorController} used to access the currently selected state
     * @param index The index of the grid cell
     * @param state The initial state of the grid cell
     * @param vertices Vertices of the grid cell
     */
    public CellShape(SimulatorController simulatorController, int index, byte state, Vector2d... vertices) {
        super();

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
        Simulator simulator = RuleSeeker.getInstance().getSimulator();
        byte state = simulatorController.getSelectedState();

        synchronized (simulator) {
            simulator.getGrid().setTileState(index, state);
        }

        updateColor(state);
    }

    /**
     * Replaces the vertices of this shape by those provided.
     *
     * @param vertices New shape vertices.
     */
    public void updateShape(Vector2d... vertices) {
        getPoints().clear();

        for (Vector2d vertex : vertices) {
            getPoints().addAll(vertex.getX(), vertex.getY());
        }
    }

    /**
     * Refreshes the color of the shape.
     */
    public void updateColor(byte state) {
        Simulator simulator = RuleSeeker.getInstance().getSimulator();

        fillProperty().setValue(simulator.getStateColoringMethod().getColors(simulator.getRuleSet()).get(state));
    }

    /**
     * Fetches the current state and updates the color.
     */
    public void updateColor() {
        Simulator simulator = RuleSeeker.getInstance().getSimulator();

        synchronized (simulator) {
            updateColor(simulator.getGrid().getTileState(index));
        }
    }

    public int getTileIndex() {
        return index;
    }
}

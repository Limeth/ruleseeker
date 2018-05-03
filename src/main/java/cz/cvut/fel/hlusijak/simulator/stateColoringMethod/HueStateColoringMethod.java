package cz.cvut.fel.hlusijak.simulator.stateColoringMethod;

import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HueStateColoringMethod implements StateColoringMethod {
    private List<Paint> cached;
    private double hueOffset;

    public HueStateColoringMethod(double hueOffset) {
        this.hueOffset = hueOffset % 1.0;
    }

    private HueStateColoringMethod() {
        // Required by Kryo
    }

    public static HueStateColoringMethod random() {
        return new HueStateColoringMethod(new Random().nextDouble());
    }

    public double getHueOffset() {
        return hueOffset;
    }

    public void setHueOffset(double hueOffset) {
        this.hueOffset = hueOffset % 1.0;
    }

    @Override
    public List<Paint> getColors(RuleSet ruleSet) {
        byte states = ruleSet.getType().getNumberOfStates();

        if (cached != null) {
            return cached;
        }

        cached = new ArrayList<>(states);

        cached.add(Color.WHITE);

        byte remainingStates = (byte) (states - 1);

        for (int hueIndex = 0; hueIndex < remainingStates; hueIndex++) {
            Color color = Color.hsb(360.0 * ((hueIndex / (double) remainingStates + hueOffset) % 1.0), 0.8, 1.0);

            cached.add(color);
        }

        return Collections.unmodifiableList(cached);
    }

    @Override
    public void clearCache() {
        this.cached = null;
    }

    @Override
    public HueStateColoringMethod copy() {
        return new HueStateColoringMethod(hueOffset);
    }
}

package cz.cvut.fel.hlusijak.simulator.stateColoringMethod;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Provides a way of manually specifying a color for each cell state of a {@link RuleSet}.
 */
public class CustomStateColoringMethod implements StateColoringMethod {
    private static final Paint PLACEHOLDER_COLOR = new RadialGradient(
            315, 1, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.MAGENTA), new Stop(1, Color.BLACK));
    private List<Paint> colors;

    /**
     * @param colors A list of colors for each cell state.
     */
    public CustomStateColoringMethod(List<Paint> colors) {
        this.colors = Lists.newArrayList(colors);
    }

    public CustomStateColoringMethod() {
        this(Collections.emptyList());
    }

    @Override
    public List<Paint> getColors(RuleSet ruleSet) {
        int states = ruleSet.getType().getNumberOfStates();
        Builder<Paint> builder = ImmutableList.builderWithExpectedSize(states);

        for (byte state = 0; state < states; state++) {
            if (state < colors.size()) {
                builder.add(colors.get(state));
            } else {
                builder.add(PLACEHOLDER_COLOR);
            }
        }

        return builder.build();
    }

    public int size() {
        return colors.size();
    }

    public List<Paint> getColors() {
        return Lists.newArrayList(colors);
    }

    public void setColors(List<Paint> colors) {
        this.colors = Lists.newArrayList(colors);
    }

    public Paint setColor(int state, Paint color) {
        return colors.set(state, color);
    }

    public void addColor(Paint color) {
        colors.add(color);
    }

    public Optional<Paint> removeLastColor() {
        if (colors.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(colors.remove(colors.size() - 1));
        }
    }

    @Override
    public void clearCache() {
        // Do nothing
    }

    @Override
    public CustomStateColoringMethod copy() {
        return new CustomStateColoringMethod(colors);
    }
}

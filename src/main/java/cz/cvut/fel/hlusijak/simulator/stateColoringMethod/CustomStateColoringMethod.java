package cz.cvut.fel.hlusijak.simulator.stateColoringMethod;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;

import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

public class CustomStateColoringMethod implements StateColoringMethod {
    private static final Paint PLACEHOLDER_COLOR = new RadialGradient(
            315, 1, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.MAGENTA), new Stop(1, Color.BLACK));
    private List<Paint> colors;

    public CustomStateColoringMethod(List<Paint> colors) {
        this.colors = Lists.newArrayList(colors);
    }

    public CustomStateColoringMethod() {
        this(Collections.emptyList());
    }

    @Override
    public List<Paint> getColors(RuleSet ruleSet) {
        int states = ruleSet.getNumberOfStates();
        Builder<Paint> builder = ImmutableList.builderWithExpectedSize(states);

        for (int state = 0; state < states; state++) {
            if (state < colors.size()) {
                builder.add(colors.get(state));
            } else {
                builder.add(PLACEHOLDER_COLOR);
            }
        }

        return builder.build();
    }

    public void setColors(List<Paint> colors) {
        this.colors = Lists.newArrayList(colors);
    }

    public Paint setColor(int state, Paint color) {
        return colors.set(state, color);
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

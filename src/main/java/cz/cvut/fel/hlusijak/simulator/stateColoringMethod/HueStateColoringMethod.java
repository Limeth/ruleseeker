package cz.cvut.fel.hlusijak.simulator.stateColoringMethod;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

public class HueStateColoringMethod implements StateColoringMethod {
    private List<Paint> cached;

    @Override
    public List<Paint> getColors(RuleSet ruleSet) {
        int states = ruleSet.getNumberOfStates();

        if (cached != null) {
            return cached;
        }

        Builder<Paint> builder = ImmutableList.builderWithExpectedSize(states);

        builder.add(Color.WHITE);

        int remainingStates = states - 1;

        for (int hueIndex = 0; hueIndex < remainingStates; hueIndex++) {
            Color color = Color.hsb(360.0 * ((hueIndex / (double) remainingStates + ruleSet.getHueOffset()) % 1.0), 0.8, 1.0);

            builder.add(color);
        }


        return cached = builder.build();
    }

    @Override
    public void clearCache() {
        this.cached = null;
    }

    @Override
    public HueStateColoringMethod copy() {
        return new HueStateColoringMethod();
    }
}

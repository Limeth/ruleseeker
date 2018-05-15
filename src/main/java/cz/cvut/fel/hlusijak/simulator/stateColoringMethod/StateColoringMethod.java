package cz.cvut.fel.hlusijak.simulator.stateColoringMethod;

import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import javafx.scene.paint.Paint;

import java.util.List;

/**
 * Used to determine the color of each state in a given simulation.
 */
public interface StateColoringMethod {
    /**
     * This method uses caching, be sure to call {@link #clearCache()} whenever updating this instance.
     *
     * @param ruleSet The {@link RuleSet} of the simulation
     * @return A list of colors with indices equal to the cell state. May be
     *         cached. See {@link #clearCache()}.
     */
    List<Paint> getColors(RuleSet ruleSet);

    /**
     * Clears cached colors. This method shall be called whenever this instance is updated.
     */
    void clearCache();

    /**
     * @return A deep copy of this {@link StateColoringMethod}.
     */
    StateColoringMethod copy();
}

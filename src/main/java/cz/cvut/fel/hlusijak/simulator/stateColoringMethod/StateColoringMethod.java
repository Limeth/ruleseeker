package cz.cvut.fel.hlusijak.simulator.stateColoringMethod;

import java.util.List;

import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import javafx.scene.paint.Paint;

public interface StateColoringMethod {
    List<Paint> getColors(RuleSet ruleSet);
    void clearCache();
    StateColoringMethod copy();
}

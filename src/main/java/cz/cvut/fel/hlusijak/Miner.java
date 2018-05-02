package cz.cvut.fel.hlusijak;

import cz.cvut.fel.hlusijak.simulator.IterationResult;
import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.util.FutureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Miner {
    private static final Logger LOGGER = LoggerFactory.getLogger(Miner.class);

    private Consumer<RuleSet> resultConsumer;
    private Random rng;

    private Simulator seed;
    private int minIterations;
    private int maxIterations;

    private Simulator currentSimulatorSeed;
    private Simulator currentSimulator;
    private CompletableFuture<?> simulation;

    public Miner(Consumer<RuleSet> resultConsumer) {
        this.resultConsumer = resultConsumer;
        this.rng = new Random();
    }

    public synchronized CompletableFuture<?> mine(Simulator seed, int minIterations, int maxIterations) {
        this.seed = seed;
        this.minIterations = minIterations;
        this.maxIterations = maxIterations;
        currentSimulatorSeed = seed;

        return startMining();
    }

    private CompletableFuture<?> startMining() {
        synchronized (this) {
            currentSimulator = currentSimulatorSeed.clone();

            currentSimulator.getRuleSet().randomizeRules(rng);

            return simulation = currentSimulator.runAsync(this::onIterationComplete);
        }
    }

    private CompletableFuture<Boolean> onIterationComplete(IterationResult iterationResult) {
        return FutureUtil.futureTaskBackground(() -> {
            int iterations = iterationResult.getIterationsCompleted();
            Grid previousGrid = iterationResult.getPreviousGrid();
            Grid nextGrid = iterationResult.getNextGrid();

                LOGGER.info("Iter #" + iterations);

            if (previousGrid.equals(nextGrid) || iterations > maxIterations) {
                LOGGER.info("Iter #" + iterations);

                if (iterations >= minIterations && iterations <= maxIterations) {
                    resultConsumer.accept(currentSimulator.getRuleSet());
                }

                startMining();
                return false;
            }

            return true;
        });
    }
}

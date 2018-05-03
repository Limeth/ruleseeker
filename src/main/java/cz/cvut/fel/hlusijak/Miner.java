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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Miner {
    private static final Logger LOGGER = LoggerFactory.getLogger(Miner.class);

    private Consumer<RuleSet> resultConsumer;
    private Random rng;
    private AtomicInteger requestId = new AtomicInteger(0); // Used to detect whether the current request has been cancelled

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
            final int originalRequestId = requestId.get();
            currentSimulator = currentSimulatorSeed.clone();

            currentSimulator.getRuleSet().randomizeRules(rng);

            return simulation = currentSimulator.runAsync(iterationResult -> onIterationComplete(iterationResult, originalRequestId));
        }
    }

    public void cancel() {
        requestId.incrementAndGet();
    }

    private CompletableFuture<Boolean> onIterationComplete(IterationResult iterationResult, int originalRequestId) {
        return FutureUtil.futureTaskBackground(() -> {
            if (requestId.get() != originalRequestId) {
                return false;
            }

            int iterations = iterationResult.getIterationsCompleted();
            Grid previousGrid = iterationResult.getPreviousGrid();
            Grid nextGrid = iterationResult.getNextGrid();

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

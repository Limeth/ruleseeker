package cz.cvut.fel.hlusijak.util;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import cz.cvut.fel.hlusijak.simulator.SimulatorApplication;

public class FutureUtil {
    private static final ExecutorService BACKGROUND_EXECUTOR = Executors.newWorkStealingPool();
    private static final ExecutorService JFX_EXECUTOR = new JFXExecutor();

    public static <T> CompletableFuture<T> futureTask(Supplier<T> supplier, ExecutorService executor) {
        // Decide whether to use JFX tasks or just regular futures, because JFX
        // is special like that. :^)
        if (SimulatorApplication.isJFXInitialized()) {
            CompletableFuture<T> future = new CompletableFuture<>();
            Task<T> task = new TaskImpl<>(supplier, future);

            task.setOnSucceeded(event -> future.complete(task.getValue()));
            task.setOnCancelled(event -> future.cancel(false));
            task.setOnFailed(event -> future.completeExceptionally(task.getException()));

            executor.submit(task);

            return future;
        } else {
            return CompletableFuture.supplyAsync(supplier, executor);
        }
    }

    public static CompletableFuture<Void> futureTask(Runnable runnable, ExecutorService executor) {
        return futureTask(() -> { runnable.run(); return null; }, executor);
    }

    public static CompletableFuture<Void> futureTaskNoop(ExecutorService executor) {
        return futureTask(() -> null, executor);
    }

    public static <T> CompletableFuture<T> futureTask(Function<? super Void, ? extends CompletionStage<T>> supplier, ExecutorService executor) {
        return futureTaskNoop(executor).thenCompose(supplier);
    }

    public static <T> CompletableFuture<T> futureTaskBackground(Supplier<T> supplier) {
        return futureTask(supplier, getBackgroundExecutor());
    }

    public static CompletableFuture<Void> futureTaskBackground(Runnable runnable) {
        return futureTask(runnable, getBackgroundExecutor());
    }

    public static <T> CompletableFuture<T> futureTaskBackground(Function<? super Void, ? extends CompletionStage<T>> supplier) {
        return futureTask(supplier, getBackgroundExecutor());
    }

    public static CompletableFuture<Void> futureTaskBackgroundNoop() {
        return futureTaskNoop(getBackgroundExecutor());
    }

    public static <T> CompletableFuture<T> futureTaskJFX(Supplier<T> supplier) {
        return futureTask(supplier, getJFXExecutor());
    }

    public static CompletableFuture<Void> futureTaskJFX(Runnable runnable) {
        return futureTask(runnable, getJFXExecutor());
    }

    public static <T> CompletableFuture<T> futureTaskJFX(Function<? super Void, ? extends CompletionStage<T>> supplier) {
        return futureTask(supplier, getJFXExecutor());
    }

    public static CompletableFuture<Void> futureTaskJFXNoop() {
        return futureTaskNoop(getJFXExecutor());
    }

    private static class TaskImpl<T> extends Task<T> {
        private final Supplier<T> supplier;

        private TaskImpl(Supplier<T> supplier, CompletableFuture<T> future) {
            this.supplier = supplier;
        }

        @Override
        protected T call() throws Exception {
            return supplier.get();
        }
    }

    public static ExecutorService getBackgroundExecutor() {
        return BACKGROUND_EXECUTOR;
    }

    public static ExecutorService getJFXExecutor() {
        return JFX_EXECUTOR;
    }

    private static class JFXExecutor extends AbstractExecutorService {
        @Override
        public void shutdown() {}

        @Override
        public List<Runnable> shutdownNow() {
            return Collections.emptyList();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void execute(Runnable command) {
            Platform.runLater(command);
        }
    }
}

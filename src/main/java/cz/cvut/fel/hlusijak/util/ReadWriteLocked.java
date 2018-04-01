package cz.cvut.fel.hlusijak.util;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ReadWriteLocked<T> {
    private final ReadWriteLock lock;
    private T instance;

    public ReadWriteLocked(ReadWriteLock lock, T instance) {
        this.lock = lock;
        this.instance = instance;
    }

    public ReadWriteLocked(ReadWriteLock lock) {
        this(lock, null);
    }

    public ReadWriteLocked(T instance) {
        this(new ReentrantReadWriteLock(), instance);
    }

    public ReadWriteLocked() {
        this(new ReentrantReadWriteLock(), null);
    }

    public void readLock(Consumer<T> consumer) {
        lock.readLock().lock();

        try {
            consumer.accept(this.instance);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void writeLock(UnaryOperator<T> operator) {
        lock.writeLock().lock();

        try {
            this.instance = operator.apply(this.instance);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void writeLock(Consumer<T> consumer) {
        writeLock(t -> {
            consumer.accept(t);
            return t;
        });
    }
}

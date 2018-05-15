package cz.cvut.fel.hlusijak.util;

/**
 * Makes it possible to introduce a level of indirection.
 * This is useful for mutating such local variables, which are declared outside
 * of lambdas.
 */
public class Wrapper<T> {
    public T value;

    public Wrapper(T value) {
        this.value = value;
    }
}

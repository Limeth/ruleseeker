package cz.cvut.fel.hlusijak.util;

/**
 * An immutable integer vector class.
 */
public class Vector2i {
    public static final Vector2i ZERO = new Vector2i(0);
    public static final Vector2i ONE = new Vector2i(1);

    private final int x, y;

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i(int d) {
        this(d, d);
    }

    public static Vector2i of(int x, int y) {
        return new Vector2i(x, y);
    }

    public static Vector2i of(int d) {
        return new Vector2i(d);
    }

    public Vector2d floatingPoint() {
        return Vector2d.of(x, y);
    }

    public Vector2i neg() {
        return new Vector2i(-x, -y);
    }

    public Vector2i add(int other) {
        return new Vector2i(x + other, y + other);
    }

    public Vector2i add(Vector2i other) {
        return new Vector2i(x + other.x, y + other.y);
    }

    public Vector2i sub(int other) {
        return new Vector2i(x - other, y - other);
    }

    public Vector2i sub(Vector2i other) {
        return new Vector2i(x - other.x, y - other.y);
    }

    public Vector2i mul(int other) {
        return new Vector2i(x * other, y * other);
    }

    public Vector2i mul(Vector2i other) {
        return new Vector2i(x * other.x, y * other.y);
    }

    public Vector2i div(int other) {
        return new Vector2i(x / other, y / other);
    }

    public Vector2i div(Vector2i other) {
        return new Vector2i(x / other.x, y / other.y);
    }

    public int min() {
        return Math.min(x, y);
    }

    public int max() {
        return Math.max(x, y);
    }

    public int normSquared() {
        return x*x + y*y;
    }

    public double norm() {
        return Math.sqrt(normSquared());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2i vector2d = (Vector2i) o;

        if (Integer.compare(vector2d.x, x) != 0) return false;
        return Integer.compare(vector2d.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        result = (int) (x ^ (x >>> 32));
        result = 31 * result + (int) (y ^ (y >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("Vector2i [%f; %f]", x, y);
    }
}

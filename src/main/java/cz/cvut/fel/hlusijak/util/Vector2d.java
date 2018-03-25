package cz.cvut.fel.hlusijak.util;

/**
 * An immutable vector class.
 */
public class Vector2d {
    public static final Vector2d ZERO = new Vector2d(0);
    public static final Vector2d ONE = new Vector2d(1);

    private final double x, y;

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d(double d) {
        this(d, d);
    }

    public static Vector2d of(double x, double y) {
        return new Vector2d(x, y);
    }

    public static Vector2d of(double d) {
        return new Vector2d(d);
    }

    public Vector2d neg() {
        return new Vector2d(-x, -y);
    }

    public Vector2d add(double other) {
        return new Vector2d(x + other, y + other);
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(x + other.x, y + other.y);
    }

    public Vector2d sub(double other) {
        return new Vector2d(x - other, y - other);
    }

    public Vector2d sub(Vector2d other) {
        return new Vector2d(x - other.x, y - other.y);
    }

    public Vector2d mul(double other) {
        return new Vector2d(x * other, y * other);
    }

    public Vector2d mul(Vector2d other) {
        return new Vector2d(x * other.x, y * other.y);
    }

    public Vector2d div(double other) {
        return new Vector2d(x / other, y / other);
    }

    public Vector2d div(Vector2d other) {
        return new Vector2d(x / other.x, y / other.y);
    }

    public double min() {
        return Math.min(x, y);
    }

    public double max() {
        return Math.max(x, y);
    }

    public double normSquared() {
        return x*x + y*y;
    }

    public double norm() {
        return Math.sqrt(normSquared());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2d vector2d = (Vector2d) o;

        if (Double.compare(vector2d.x, x) != 0) return false;
        return Double.compare(vector2d.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

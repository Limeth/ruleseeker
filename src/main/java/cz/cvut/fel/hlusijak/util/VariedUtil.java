package cz.cvut.fel.hlusijak.util;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class VariedUtil {
    /**
     * Uses {@param rng} to generate random numbers in the range <0; {@param bound}) for
     * each entry of {@param result}.
     */
    public static void randomBoundedByteArray(Random rng, byte[] result, int bound) {
        if (bound < 50 || Math.floorMod(256, bound) == 0) {
            // The smaller the bound is, the more random it is.
            // Works well for this projects usage, but is far from secure.
            // The concern here is speed.
            rng.nextBytes(result);

            IntStream.range(0, result.length)
                .parallel()
                .forEach(i -> {
                    if (result[i] < 0) {
                        result[i] = (byte) -(result[i] + 1); // Can't just reverse, because -128 * (-1) results in -128
                    }

                    result[i] %= bound; // Good enough
                });
        } else {
            // Otherwise, use the good ol' bounded nextInt method.
            IntStream.range(0, result.length)
                .parallel()
                .forEach(i -> {
                    result[i] = (byte) rng.nextInt(bound);
                });
        }
    }

    /**
     * @return A stream over all byte values in the range <startInclusive; endExclusive)
     */
    public static Stream<Byte> byteStreamRange(byte startInclusive, byte endExclusive) {
        byte length = (byte) (endExclusive - startInclusive);

        return Stream.iterate(startInclusive, prev -> (byte) (prev + 1)).limit(length);
    }

    // I would've made this a generic method, but the Java type system sucks.
    /**
     * @return A slice of the array at the given {@param offset} with the maximum length of {@param maxLength}.
     */
    public static byte[] byteSlice(byte[] array, int offset, int maxLength) {
        int length = Math.max(0, Math.min(array.length - offset, maxLength));
        byte[] result = new byte[length];

        if (length <= 0) {
            return result;
        }

        System.arraycopy(array, offset, result, 0, length);

        return result;
    }
}

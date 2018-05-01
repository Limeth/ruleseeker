package cz.cvut.fel.hlusijak.util;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class VariedUtil {
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
                        result[i] *= -1;
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

    public static Stream<Byte> byteStreamRange(byte startInclusive, byte endExclusive) {
        byte length = (byte) (endExclusive - startInclusive);

        return Stream.iterate(startInclusive, prev -> (byte) (prev + 1)).limit(length);
    }
}

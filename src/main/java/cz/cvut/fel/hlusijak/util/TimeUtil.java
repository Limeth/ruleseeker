package cz.cvut.fel.hlusijak.util;

import java.time.Duration;

public class TimeUtil {
    /**
     * Derives a {@link Duration} from seconds.
     */
    public static Duration ofSeconds(double secondsDouble) {
        long seconds = (long) Math.floor(secondsDouble);
        long nanoseconds = (long) ((secondsDouble - seconds) * 1_000_000_000);

        return Duration.ofSeconds(seconds, nanoseconds);
    }
}

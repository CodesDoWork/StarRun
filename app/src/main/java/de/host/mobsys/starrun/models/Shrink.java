package de.host.mobsys.starrun.models;

import java.time.Duration;

/**
 * Class to handle shrink (power-up) related functionality
 */
public class Shrink {
    private static final Duration SHRINK_DURATION = Duration.ofSeconds(10);

    private Duration remainingDuration = Duration.ZERO;

    public void update(Duration elapsedTime) {
        remainingDuration = remainingDuration.minus(elapsedTime);
    }

    public void enable() {
        remainingDuration = SHRINK_DURATION;
    }

    public void disable() {
        remainingDuration = Duration.ZERO;
    }

    public boolean isEnabled() {
        return !(remainingDuration.isNegative() || remainingDuration.isZero());
    }
}

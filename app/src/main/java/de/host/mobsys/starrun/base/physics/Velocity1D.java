package de.host.mobsys.starrun.base.physics;

import androidx.annotation.NonNull;

import java.time.Duration;

/**
 * Represents a scalar velocity as 1D vector with a duration.
 */
public class Velocity1D {
    public static Velocity1D ZERO = new Velocity1D(0);

    private final float valuePerSecond;

    public Velocity1D(float valuePerSecond) {
        this(valuePerSecond, Duration.ofSeconds(1));
    }

    public Velocity1D(float value, Duration duration) {
        valuePerSecond = value / DurationUtils.toSeconds(duration);
    }

    public float getValue(@NonNull Duration duration) {
        return valuePerSecond * DurationUtils.toSeconds(duration);
    }
}

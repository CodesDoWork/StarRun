package de.host.mobsys.starrun.base.physics;

import androidx.annotation.NonNull;

import java.time.Duration;

import de.host.mobsys.starrun.base.size.SizeVector2D;

public class Velocity extends SizeVector2D {
    public static Velocity ZERO = new Velocity(0, 0);

    public Velocity(float xPerSecond, float yPerSecond) {
        this(xPerSecond, yPerSecond, Duration.ofSeconds(1));
    }

    public Velocity(float x, float y, Duration duration) {
        super(x / DurationUtils.toSeconds(duration), y / DurationUtils.toSeconds(duration));
    }

    public float getX(@NonNull Duration duration) {
        return x * DurationUtils.toSeconds(duration);
    }

    public float getY(@NonNull Duration duration) {
        return y * DurationUtils.toSeconds(duration);
    }
}

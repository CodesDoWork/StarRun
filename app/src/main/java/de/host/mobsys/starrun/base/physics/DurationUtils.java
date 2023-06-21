package de.host.mobsys.starrun.base.physics;

import java.time.Duration;

public abstract class DurationUtils {

    public static float toSeconds(Duration duration) {
        return (float) duration.toNanos() / Duration.ofSeconds(1).toNanos();
    }
}

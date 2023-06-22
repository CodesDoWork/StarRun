package de.host.mobsys.starrun.base.physics;

import java.time.Duration;

public class VelocityBuilder {

    private float x = 0;
    private float y = 0;
    private Duration duration = Duration.ofSeconds(1);

    public VelocityBuilder up(float up) {
        y -= up;
        return this;
    }

    public VelocityBuilder down(float down) {
        y += down;
        return this;
    }

    public VelocityBuilder left(float left) {
        x -= left;
        return this;
    }

    public VelocityBuilder right(float right) {
        x += right;
        return this;
    }

    public VelocityBuilder setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public Velocity build() {
        return new Velocity(x, y, duration);
    }
}

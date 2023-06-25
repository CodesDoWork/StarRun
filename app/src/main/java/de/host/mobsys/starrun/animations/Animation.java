package de.host.mobsys.starrun.animations;

import android.graphics.Canvas;

import java.time.Duration;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.size.Rect;

/**
 * The Animation class is used to represent and manage animations in the game. It provides a basic
 * structure for updating and drawing animations.
 */
public abstract class Animation extends GameObject {

    public Animation(Rect rect) {
        super(rect);
    }

    public Animation(Rect rect, Velocity velocity) {
        super(rect, velocity);
    }

    @Override
    public void update(Duration frameDuration) {
        super.update(frameDuration);
    }

    @Override
    public abstract void draw(Canvas canvas);
}


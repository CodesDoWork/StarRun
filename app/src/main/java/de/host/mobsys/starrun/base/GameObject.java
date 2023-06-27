package de.host.mobsys.starrun.base;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.size.Position;

/**
 * This class represents a game entity. Any object inside a game inherits from this class and is
 * added to a GameLayer.
 */
public abstract class GameObject implements GlobalTouchListener {

    protected final Position position;
    private final List<OnDestroyListener> onDestroyListeners = new ArrayList<>();
    protected Velocity velocity = Velocity.ZERO;

    public GameObject(Position position) {
        this.position = position;
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    @Override
    public void onGlobalTouchEvent(MotionEvent event) {
    }

    public void update(Duration elapsedTime) {
        position.translate(velocity.getX(elapsedTime), velocity.getY(elapsedTime));
    }

    public abstract void draw(Canvas canvas);

    public void destroy() {
        onDestroyListeners.forEach(OnDestroyListener::onDestroy);
    }

    public void addOnDestroyListener(OnDestroyListener listener) {
        onDestroyListeners.add(listener);
    }

    @FunctionalInterface
    public interface OnDestroyListener {
        void onDestroy();
    }
}

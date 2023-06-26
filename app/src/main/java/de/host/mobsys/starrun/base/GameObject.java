package de.host.mobsys.starrun.base;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.size.Rect;

/**
 * This class represents a game entity. Any object inside a game inherits from this class and is
 * added to a GameLayer.
 */
public abstract class GameObject implements GameTouchListener {

    protected final Rect rect;
    private final List<OnDestroyListener> onDestroyListeners = new ArrayList<>();
    protected Velocity velocity;

    public GameObject(Rect rect) {
        this(rect, Velocity.ZERO);
    }

    public GameObject(Rect rect, Velocity velocity) {
        this.rect = rect;
        this.velocity = velocity;
    }

    @Override
    public void onGlobalTouchEvent(MotionEvent event) {
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
    }

    public void update(Duration frameDuration) {
        rect.translate(velocity.getX(frameDuration), velocity.getY(frameDuration));
    }

    public abstract void draw(Canvas canvas);

    public void destroy() {
        onDestroyListeners.forEach(OnDestroyListener::onDestroy);
    }

    public void addOnDestroyListener(OnDestroyListener listener) {
        onDestroyListeners.add(listener);
    }

    public Rect getRect() {
        return rect;
    }

    @FunctionalInterface
    public interface OnDestroyListener {
        void onDestroy();
    }
}

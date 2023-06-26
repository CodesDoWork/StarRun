package de.host.mobsys.starrun.base;

import android.graphics.Point;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

import de.host.mobsys.starrun.base.collision.PolygonPixelIterator;
import de.host.mobsys.starrun.base.size.Rect;

/**
 * Interface to implement if a GameObject should be able to be touched.
 */
public interface CollidingGameObject {
    /**
     * Called when a touch event on the view is received.
     */
    default void onTouchEvent(MotionEvent event) {
    }

    boolean containsCoordinates(int x, int y);

    default boolean collidesWith(@NonNull CollidingGameObject other) {
        Point[] intersectionPoints = getRect().intersectPx(other.getRect());
        if (intersectionPoints.length == 0) {
            return false;
        }

        AtomicBoolean result = new AtomicBoolean(false);
        new PolygonPixelIterator().iteratePolygonPixels(intersectionPoints, (x, y) -> {
            boolean isColliding = containsCoordinates(x, y) && other.containsCoordinates(x, y);
            result.set(isColliding);
            return isColliding;
        });

        return result.get();
    }

    default void onCollision(@NonNull CollidingGameObject other) {
    }

    Rect getRect();
}

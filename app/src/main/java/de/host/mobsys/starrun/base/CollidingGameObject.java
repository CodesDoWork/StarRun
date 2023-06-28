package de.host.mobsys.starrun.base;

import android.graphics.Point;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicReference;

import de.host.mobsys.starrun.base.collision.PolygonPixelIterator;
import de.host.mobsys.starrun.base.size.Rect;

/**
 * Interface to implement if a GameObject should be able to be touched.
 */
public interface CollidingGameObject {
    /**
     * Called when a touch event on the view is received.
     *
     * @return whether the event is consumed or not
     */
    default boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    boolean containsCoordinates(int x, int y);

    /**
     * @return null if no collision is detected between this and the other object.
     */
    default Point getCollisionPoint(@NonNull CollidingGameObject other) {
        Point[] intersectionPoints = getRect().intersectPx(other.getRect());
        if (intersectionPoints.length == 0) {
            return null;
        }

        AtomicReference<Point> result = new AtomicReference<>(null);
        new PolygonPixelIterator().iteratePolygonPixels(intersectionPoints, (x, y) -> {
            boolean isColliding = containsCoordinates(x, y) && other.containsCoordinates(x, y);
            if (isColliding) {
                result.set(new Point(x, y));
            }

            return isColliding;
        });

        return result.get();
    }

    default void onCollision(@NonNull CollidingGameObject other, Point point) {
    }

    Rect getRect();
}

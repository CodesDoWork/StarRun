package de.host.mobsys.starrun.base;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import de.host.mobsys.starrun.base.size.Position;

/**
 * Layer with GameObjects to be added to a GameView.
 */
public class GameLayer {

    protected final List<GameObject> gameObjects = new ArrayList<>();

    private final Position position = new Position(0, 0);

    public void add(GameObject gameObject) {
        gameObjects.add(gameObject);
        gameObject.addOnDestroyListener(() -> gameObjects.remove(gameObject));
    }

    public void update(Duration elapsedTime) {
        getGameObjects().forEach(gameObject -> gameObject.update(elapsedTime));
    }

    public void draw(Canvas canvas) {
        canvas.translate(position.getXPx(), position.getYPx());
        getGameObjects().forEach(gameObject -> gameObject.draw(canvas));
        canvas.translate(-position.getXPx(), -position.getYPx());
    }

    public void translate(float x, float y) {
        position.translate(x, y);
    }

    public void onGlobalTouchEvent(MotionEvent event) {
        getGameObjects().forEach(gameObject -> gameObject.onGlobalTouchEvent(event));
    }

    public boolean onTouchEvent(MotionEvent event) {
        Point touchedPoint = new Point((int) event.getX(), (int) event.getY());

        // go through list in reverse order to go from top to bottom views.
        ListIterator<GameObject> objectsIterator =
            getGameObjects().listIterator(gameObjects.size());
        while (objectsIterator.hasPrevious()) {
            GameObject gameObject = objectsIterator.previous();
            if (gameObject instanceof CollidingGameObject touchableGameObject
                && touchableGameObject.containsCoordinates(touchedPoint.x, touchedPoint.y)) {
                touchableGameObject.onTouchEvent(event);
                return true;
            }
        }

        return false;
    }

    protected ArrayList<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }
}

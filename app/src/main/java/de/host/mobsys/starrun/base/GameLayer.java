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
    private Status status = Status.Enabled;

    public void add(GameObject gameObject) {
        gameObjects.add(gameObject);
        gameObject.addOnDestroyListener(() -> gameObjects.remove(gameObject));
    }

    public void update(Duration elapsedTime) {
        if (status != Status.Enabled) {
            return;
        }

        getGameObjects().forEach(gameObject -> gameObject.update(elapsedTime));
    }

    public void draw(Canvas canvas) {
        if (status == Status.Disabled) {
            return;
        }

        canvas.translate(position.getXPx(), position.getYPx());
        getGameObjects().forEach(gameObject -> gameObject.draw(canvas));
        canvas.translate(-position.getXPx(), -position.getYPx());
    }

    public void setStatus(Status status) {
        this.status = status;
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
        List<CollidingGameObject> touchableObjects = getCollidingGameObjects();
        ListIterator<CollidingGameObject> objectsIterator =
            touchableObjects.listIterator(touchableObjects.size());
        while (objectsIterator.hasPrevious()) {
            CollidingGameObject gameObject = objectsIterator.previous();
            if (gameObject.containsCoordinates(touchedPoint.x, touchedPoint.y)
                && gameObject.onTouchEvent(event)) {
                return true;
            }
        }

        return false;
    }

    protected List<GameObject> getGameObjects() {
        return new ArrayList<>(gameObjects);
    }

    protected List<CollidingGameObject> getCollidingGameObjects() {
        return List.of(getGameObjects().stream()
                                       .filter(gameObject -> gameObject instanceof CollidingGameObject)
                                       .toArray(CollidingGameObject[]::new));
    }

    public enum Status {
        Enabled,
        DrawEnabled,
        Disabled
    }
}

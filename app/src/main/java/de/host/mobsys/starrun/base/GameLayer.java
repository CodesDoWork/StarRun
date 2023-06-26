package de.host.mobsys.starrun.base;

import android.graphics.Canvas;
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

    private final List<GameObject> gameObjects = new ArrayList<>();
    private final List<GameObject> gameObjectsToRemove = new ArrayList<>();

    private final Position position = new Position(0, 0);

    public void add(GameObject gameObject) {
        gameObjects.add(gameObject);
        gameObject.addOnDestroyListener(() -> gameObjectsToRemove.add(gameObject));
    }

    public void update(Duration frameDuration) {
        // Use indexed loop and size before because objects can be added during updates.
        int gameObjectsSize = gameObjects.size();
        for (int i = 0; i < gameObjectsSize; ++i) {
            gameObjects.get(i).update(frameDuration);
        }

        gameObjectsToRemove.forEach(gameObjects::remove);
        gameObjectsToRemove.clear();
    }

    public void draw(Canvas canvas) {
        canvas.translate(position.getXPx(), position.getYPx());
        gameObjects.forEach(gameObject -> gameObject.draw(canvas));
        canvas.translate(-position.getXPx(), -position.getYPx());
    }

    public void translate(float x, float y) {
        position.translate(x, y);
    }

    public void onGlobalTouchEvent(MotionEvent event) {
        gameObjects.forEach(gameObject -> gameObject.onGlobalTouchEvent(event));
    }

    public boolean onTouchEvent(MotionEvent event) {
        ListIterator<GameObject> objectsIterator = gameObjects.listIterator(gameObjects.size());
        while (objectsIterator.hasNext()) {
            GameObject gameObject = objectsIterator.previous();
            if (gameObject.rect.contains(event.getX(), event.getY())) {
                gameObject.onTouchEvent(event);
                return true;
            }
        }

        return false;
    }
}

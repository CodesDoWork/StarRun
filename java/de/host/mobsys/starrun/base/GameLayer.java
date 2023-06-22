package de.host.mobsys.starrun.base;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import de.host.mobsys.starrun.GameActivity;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.views.Background;
import de.host.mobsys.starrun.views.CollisionObject;
import de.host.mobsys.starrun.views.Player;

public class GameLayer {

    private final List<GameObject> gameObjects = new ArrayList<>();
    private final List<GameObject> gameObjectsToRemove = new ArrayList<>();

    private final Position position = new Position(0, 0);

    public static boolean collided = false;
    public void add(GameObject gameObject) {
        gameObjects.add(gameObject);
        gameObject.addOnDestroyListener(() -> gameObjectsToRemove.add(gameObject));
    }

    public void update(Duration frameDuration) {
        if (!collided) {
            // Use indexed loop and size before because objects can be added during updates.
            int gameObjectsSize = gameObjects.size();

            for (int i = 0; i < gameObjectsSize; ++i) {
                gameObjects.get(i).update(frameDuration);
            }

            checkCollisions();

            gameObjectsToRemove.forEach(gameObjects::remove);
            gameObjectsToRemove.clear();
        }
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



    public void checkCollisions() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject objectA = gameObjects.get(i);
            for (int j = i + 1; j < gameObjects.size(); j++) {
                GameObject objectB = gameObjects.get(j);
                if (objectA.collidesWith(objectB.rect)) {
                    // Kollision zwischen objectA und objectB erkannt.
                    // Hier können Sie entsprechende Aktionen ausführen.
                    // handleCollision();
                    Log.d("Collision", "Eine Kollision ist aufgetreten!");
                    collided = true;

                }
            }
        }
    }



}


package de.host.mobsys.starrun.base.views;

import android.graphics.Point;

import java.time.Duration;
import java.util.List;

import de.host.mobsys.starrun.base.CollidingGameObject;
import de.host.mobsys.starrun.base.GameLayer;

public class CollisionLayer extends GameLayer {

    @Override
    public void update(Duration elapsedTime) {
        super.update(elapsedTime);

        List<CollidingGameObject> collidingObjects = getCollidingGameObjects();
        for (int i = 0; i < collidingObjects.size(); ++i) {
            CollidingGameObject collidingObject = collidingObjects.get(i);
            for (int j = i + 1; j < collidingObjects.size(); ++j) {
                CollidingGameObject testObject = collidingObjects.get(j);
                Point collisionPoint = collidingObject.getCollisionPoint(testObject);
                if (collisionPoint != null) {
                    collidingObject.onCollision(testObject, collisionPoint);
                    testObject.onCollision(collidingObject, collisionPoint);
                }
            }
        }
    }
}

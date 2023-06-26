package de.host.mobsys.starrun.base.views;

import java.time.Duration;

import de.host.mobsys.starrun.base.CollidingGameObject;
import de.host.mobsys.starrun.base.GameLayer;

public class CollisionLayer extends GameLayer {

    @Override
    public void update(Duration elapsedTime) {
        super.update(elapsedTime);

        CollidingGameObject[] collidingObjects = getGameObjects().stream()
                                                                 .filter(gameObject -> gameObject instanceof CollidingGameObject)
                                                                 .toArray(CollidingGameObject[]::new);

        for (int i = 0; i < collidingObjects.length; ++i) {
            CollidingGameObject collidingObject = collidingObjects[i];
            for (int j = i + 1; j < collidingObjects.length; ++j) {
                CollidingGameObject testObject = collidingObjects[j];
                if (collidingObject.collidesWith(testObject)) {
                    collidingObject.onCollision(testObject);
                    testObject.onCollision(collidingObject);
                }
            }
        }
    }
}

package de.host.mobsys.starrun.views;

import android.util.Log;

import java.time.Duration;

import de.host.mobsys.starrun.base.GameLayer;
import de.host.mobsys.starrun.base.GameObject;

public class CollisionLayer extends GameLayer {

    @Override
    public void update(Duration frameDuration) {
        super.update(frameDuration);

        gameObjects.forEach(gameObject -> {
            for (GameObject testObject : gameObjects) {
                if (gameObject == testObject) {
                    continue;
                }

                if (gameObject.getRect().collidesWith(testObject.getRect())) {
                    Log.d("Collision", "Collided");
                }
            }
        });
    }

    private boolean areTrulyColliding(BitmapObject o1, BitmapObject o2) {
        return false;
    }
}

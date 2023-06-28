package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.host.mobsys.starrun.base.CollidingGameObject;
import de.host.mobsys.starrun.base.physics.Velocity1D;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.views.BitmapObject;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.control.RandomUtils;
import de.host.mobsys.starrun.models.Difficulty;

public class Obstacle extends BitmapObject {

    private static final List<Rect> lastObstacleSpawns = new ArrayList<>();

    private Obstacle(Rect rect, Bitmap sprite) {
        super(rect, sprite);
    }

    /**
     * Creates a new Obstacle with random properties.
     *
     * @param assets     Assets to get a random obstacle sprite
     * @param difficulty Difficulty to enable difficulty adjustments
     * @return The created Obstacle
     */
    public static Obstacle createRandom(Assets assets, Difficulty difficulty) {
        Random random = new Random();
        Bitmap sprite = assets.getRandomObstacle();

        float minHeight = 5 * difficulty.getHalf();
        float maxHeight = 25;
        float height = RandomUtils.between(minHeight, maxHeight);

        float minY = 2 + minHeight - height;
        float maxY = 98 - minHeight;

        Rect rect;
        int tries = 0;
        int maxTries = 3;
        do {
            float y = RandomUtils.between(minY, maxY);
            Position position = new Position(110, y);
            rect = new Rect(position, BitmapUtils.getSizeByHeight(sprite, height));
        } while (++tries < maxTries && isCollidingSpawningArea(rect));
        Obstacle obstacle = new Obstacle(rect, sprite);
        Log.d("OBSTACLE", "Tries: " + tries);

        lastObstacleSpawns.add(rect);
        if (lastObstacleSpawns.size() > 3) {
            lastObstacleSpawns.remove(0);
        }

        float minSpeed = 4 * difficulty.getHalf();
        float maxSpeed = 22.5f - height / 3f;
        float speed = RandomUtils.between(minSpeed, maxSpeed) * difficulty.get();
        obstacle.setVelocity(new VelocityBuilder().left(speed).build());

        obstacle.setRotation(RandomUtils.between(0, 360));

        int minSpin = 5;
        int maxSpin = 90;
        float spin = RandomUtils.between(minSpin, maxSpin);
        spin *= random.nextBoolean() ? 1 : -1;
        obstacle.rotationSpeed = new Velocity1D(spin);

        return obstacle;
    }

    private static boolean isCollidingSpawningArea(Rect rect) {
        for (Rect lastObstacleSpawn : lastObstacleSpawns) {
            if (rect.getTop() <= lastObstacleSpawn.getBottom()
                && rect.getBottom() >= lastObstacleSpawn.getTop()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void update(Duration elapsedTime) {
        super.update(elapsedTime);
        if (rect.getRightPx() < 0) {
            destroy();
        }
    }

    @Override
    public void onCollision(@NonNull CollidingGameObject other, Point point) {
        destroy();
    }
}

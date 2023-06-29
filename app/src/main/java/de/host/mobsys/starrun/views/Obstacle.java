package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Point;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.R;
import de.host.mobsys.starrun.base.CollidingGameObject;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.views.BitmapObject;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.control.RandomUtils;
import de.host.mobsys.starrun.control.Sounds;
import de.host.mobsys.starrun.models.Difficulty;

public class Obstacle extends BitmapObject {
    private static final float BASE_MIN_HEIGHT = 5;
    private static final float MAX_HEIGHT = 25;
    private static final float BASE_MIN_Y = 2;
    private static final float BASE_MAX_Y = 98;
    private static final int MAX_PLACEMENT_TRIES = 3;
    private static final float MAX_BASE_SPEED = 22.5f;
    private static final int OBSTACLES_TO_RESPECT = 3;
    private static final List<Rect> LAST_OBSTACLE_SPAWNS = new ArrayList<>();

    private final Sounds sounds;

    private Obstacle(Rect rect, Bitmap sprite, Sounds sounds) {
        super(rect, sprite);
        this.sounds = sounds;
    }

    /**
     * Creates a new Obstacle with random properties.
     *
     * @param assets     Assets to get a random obstacle sprite
     * @param difficulty Difficulty to enable difficulty adjustments
     * @return The created Obstacle
     */
    public static Obstacle createRandom(Assets assets, Difficulty difficulty, Sounds sounds) {
        Bitmap sprite = assets.getRandomObstacle();
        Spawner spawner = new Spawner(difficulty, sprite);

        Rect rect = getRandomRect(difficulty, spawner);
        Obstacle obstacle = new Obstacle(rect, sprite, sounds);

        spawner.setSpeed(obstacle, MAX_BASE_SPEED);
        spawner.rotate(obstacle);

        return obstacle;
    }

    private static Rect getRandomRect(Difficulty difficulty, Spawner spawner) {
        float minHeight = BASE_MIN_HEIGHT * difficulty.getHalf();
        float height = RandomUtils.between(minHeight, MAX_HEIGHT);
        float minY = BASE_MIN_Y + minHeight - height;
        float maxY = BASE_MAX_Y - minHeight;

        Rect rect;
        int tries = 0;
        do {
            rect = spawner.createRect(minY, maxY, height);
        } while (++tries < MAX_PLACEMENT_TRIES && isCollidingSpawningArea(rect));

        LAST_OBSTACLE_SPAWNS.add(rect);
        if (LAST_OBSTACLE_SPAWNS.size() > OBSTACLES_TO_RESPECT) {
            LAST_OBSTACLE_SPAWNS.remove(0);
        }

        return rect;
    }

    private static boolean isCollidingSpawningArea(Rect rect) {
        for (Rect lastObstacleSpawn : LAST_OBSTACLE_SPAWNS) {
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
        super.onCollision(other, point);

        if (!(other instanceof PowerUpView)) {
            sounds.playSound(R.raw.explosion);
            destroy();
        }
    }
}

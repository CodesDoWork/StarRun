package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.time.Duration;
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

public class Obstacle extends BitmapObject {

    private Obstacle(Rect rect, Bitmap sprite) {
        super(rect, sprite);
    }

    public static Obstacle createRandom(Assets assets) {
        Random random = new Random();
        Bitmap sprite = assets.getRandomObstacle();

        int minHeight = 5;
        int maxHeight = 25;
        float height = RandomUtils.between(minHeight, maxHeight);

        float minY = minHeight - height;
        float maxY = 100 - minHeight;
        float y = RandomUtils.between(minY, maxY);
        Position position = new Position(100, y);
        Rect rect = new Rect(position, BitmapUtils.getSizeByHeight(sprite, height));
        Obstacle obstacle = new Obstacle(rect, sprite);

        int minSpeed = 4;
        int maxSpeed = 20;
        float speed = RandomUtils.between(minSpeed, maxSpeed);
        obstacle.setVelocity(new VelocityBuilder().left(speed).build());

        obstacle.setRotation(RandomUtils.between(0, 360));

        int minSpin = 5;
        int maxSpin = 90;
        float spin = RandomUtils.between(minSpin, maxSpin);
        spin *= random.nextBoolean() ? 1 : -1;
        obstacle.rotationSpeed = new Velocity1D(spin);

        return obstacle;
    }

    @Override
    public void update(Duration elapsedTime) {
        super.update(elapsedTime);
        if (rect.getRightPx() < 0) {
            destroy();
        }
    }

    @Override
    public void onCollision(@NonNull CollidingGameObject other) {
        destroy();
    }
}

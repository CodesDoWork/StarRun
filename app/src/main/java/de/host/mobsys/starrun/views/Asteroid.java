package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;

import java.time.Duration;
import java.util.Random;

import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.control.Assets;

public class Asteroid extends BitmapObject {

    private Asteroid(
        Rect rect,
        Velocity velocity,
        float rotationSpeed,
        Bitmap sprite
    ) {
        super(rect, velocity, rotationSpeed, sprite);
    }

    public static Asteroid createRandom(Assets assets) {
        Random random = new Random();
        Bitmap sprite = assets.getRandomAsteroid();

        int minHeight = 3;
        int maxHeight = 25;

        float height = minHeight + random.nextFloat() * (maxHeight - minHeight + 1);
        Size size = Size.fromHeightAndAspectRatio(height, 2);

        float y = (random.nextFloat() * 100) - 5;
        Position position = new Position(100, y);

        float rotation = random.nextFloat() * 360;
        float rotationSpeed = 0.1f + random.nextFloat() * 2;
        rotationSpeed *= random.nextBoolean() ? 1 : -1;

        float speed = random.nextFloat() * 15 + 4;
        Velocity velocity = new VelocityBuilder().left(speed).build();

        return new Asteroid(new Rect(position, size, rotation), velocity, rotationSpeed, sprite);
    }

    @Override
    public void update(Duration frameDuration) {
        super.update(frameDuration);
        if (rect.getRightPx() < 0) {
            destroy();
        }
    }
}

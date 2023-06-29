package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.Random;

import de.host.mobsys.starrun.base.physics.Velocity1D;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.base.views.BitmapObject;
import de.host.mobsys.starrun.control.RandomUtils;
import de.host.mobsys.starrun.models.Difficulty;

/**
 * Class to provide functionality for spawning objects at the end of the screen, which move towards
 * the player.
 */
public class Spawner {
    private static final float BASE_MIN_SPEED = 4;
    private static final float SPEED_HEIGHT_DIVISOR = 3;
    private static final float MIN_ROTATION = 0;
    private static final float MAX_ROTATION = 360;
    private static final float MIN_SPIN = 5;
    private static final float MAX_SPIN = 90;

    private final Random random = new Random();
    private final Difficulty difficulty;
    private final Bitmap sprite;

    public Spawner(Difficulty difficulty, Bitmap sprite) {
        this.difficulty = difficulty;
        this.sprite = sprite;
    }

    public Rect createRect(float minY, float maxY, float height) {
        float y = RandomUtils.between(minY, maxY);
        Position position = new Position(SizeSystem.getMaxWidthUnits(), y);
        return new Rect(position, BitmapUtils.getSizeByHeight(sprite, height));
    }

    public void setSpeed(@NonNull BitmapObject gameObject, float maxBaseSpeed) {
        float height = gameObject.getRect().size.getY();

        float minSpeed = BASE_MIN_SPEED * difficulty.getHalf();
        float maxSpeed = maxBaseSpeed - height / SPEED_HEIGHT_DIVISOR;
        float speed = RandomUtils.between(minSpeed, maxSpeed) * difficulty.get();
        gameObject.setVelocity(new VelocityBuilder().left(speed).build());
    }

    public void rotate(@NonNull BitmapObject obj) {
        obj.setRotation(RandomUtils.between(MIN_ROTATION, MAX_ROTATION));
        float spin = RandomUtils.between(MIN_SPIN, MAX_SPIN);
        spin *= random.nextBoolean() ? 1 : -1;
        obj.setRotationSpeed(new Velocity1D(spin));
    }
}

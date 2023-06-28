package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Point;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.Random;

import de.host.mobsys.starrun.R;
import de.host.mobsys.starrun.base.CollidingGameObject;
import de.host.mobsys.starrun.base.physics.Velocity1D;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.views.BitmapObject;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.control.RandomUtils;
import de.host.mobsys.starrun.control.Sounds;
import de.host.mobsys.starrun.models.Difficulty;
import de.host.mobsys.starrun.models.PowerUp;

public class PowerUpView extends BitmapObject {

    public final PowerUp powerUp;
    private final Sounds sounds;

    private PowerUpView(Rect rect, Bitmap sprite, Sounds sounds, PowerUp powerUp) {
        super(rect, sprite);
        this.sounds = sounds;
        this.powerUp = powerUp;
    }

    /**
     * Creates a new PowerUp with random properties.
     *
     * @param assets     Assets to get the sprites
     * @param difficulty Difficulty to enable difficulty adjustments
     * @return The created PowerUp
     */
    public static PowerUpView createRandom(Assets assets, Difficulty difficulty, Sounds sounds) {
        Random random = new Random();

        PowerUp powerUp = PowerUp.values()[random.nextInt(PowerUp.values().length)];
        Bitmap sprite = assets.readBitmap(powerUp.asset);

        float height = 7.5f;
        int minY = 2;
        int maxY = 90;
        float y = RandomUtils.between(minY, maxY);
        Position position = new Position(110, y);
        Rect rect = new Rect(position, BitmapUtils.getSizeByHeight(sprite, height));

        PowerUpView powerUpView = new PowerUpView(rect, sprite, sounds, powerUp);

        float minSpeed = 4 * difficulty.getHalf();
        float maxSpeed = 15 - height / 3f;
        float speed = RandomUtils.between(minSpeed, maxSpeed) * difficulty.get();
        powerUpView.setVelocity(new VelocityBuilder().left(speed).build());

        if (powerUp != PowerUp.Shrink) {
            powerUpView.setRotation(RandomUtils.between(0, 360));

            int minSpin = 5;
            int maxSpin = 90;
            float spin = RandomUtils.between(minSpin, maxSpin);
            spin *= random.nextBoolean() ? 1 : -1;
            powerUpView.rotationSpeed = new Velocity1D(spin);
        }

        return powerUpView;
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

        if (!(other instanceof Player)) {
            sounds.playSound(R.raw.no_power_up);
        }

        destroy();
    }
}

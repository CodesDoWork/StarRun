package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Point;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.Random;

import de.host.mobsys.starrun.R;
import de.host.mobsys.starrun.base.CollidingGameObject;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.views.BitmapObject;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.control.Sounds;
import de.host.mobsys.starrun.models.Difficulty;
import de.host.mobsys.starrun.models.PowerUp;

public class PowerUpView extends BitmapObject {
    private static final float HEIGHT = 7.5f;
    private static final float MIN_Y = 2;
    private static final float MAX_Y = 90;
    private static final float MAX_BASE_SPEED = 15;

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
        Spawner spawner = new Spawner(difficulty, sprite);

        Rect rect = spawner.createRect(MIN_Y, MAX_Y, HEIGHT);
        PowerUpView powerUpView = new PowerUpView(rect, sprite, sounds, powerUp);
        spawner.setSpeed(powerUpView, MAX_BASE_SPEED);
        if (powerUp != PowerUp.Shrink) {
            spawner.rotate(powerUpView);
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

package de.host.mobsys.starrun.views;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.R;
import de.host.mobsys.starrun.base.CollidingGameObject;
import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.views.SpriteSheetObject;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.control.Sounds;
import de.host.mobsys.starrun.models.Difficulty;
import de.host.mobsys.starrun.models.PowerUp;
import de.host.mobsys.starrun.models.Shrink;

/**
 * Class to handle the player
 */
public class Player extends SpriteSheetObject {
    private static final float UP_SPEED = 70;
    private static final float DOWN_SPEED = 40;
    private static final float SHRINK_FACTOR = 2;

    private final List<OnMoveListener> onMoveListeners = new ArrayList<>();
    private final Shield shield;
    private final Sounds sounds;
    private final Shrink shrink = new Shrink();

    private Velocity down = new VelocityBuilder().down(DOWN_SPEED).build();
    private Velocity up = new VelocityBuilder().up(UP_SPEED).build();

    public Player(Rect rect, Assets assets, Difficulty difficulty, Sounds sounds) {
        super(rect, assets.getPlayerSpriteSheet(), 8, LoopPolicy.Bounce, Duration.ofMillis(2000));
        this.shield = new Shield(assets.getShieldBitmap(), rect);
        this.sounds = sounds;
        velocity = down;

        difficulty.addChangeListener(value -> {
            down = new VelocityBuilder().down(DOWN_SPEED * difficulty.getHalf()).build();
            up = new VelocityBuilder().up(UP_SPEED * difficulty.getHalf()).build();
        });
    }

    @Override
    public void update(Duration elapsedTime) {
        float y = rect.getTop();
        super.update(elapsedTime);

        rect.ensureInScreen();
        float moved = rect.getTop() - y;
        if (moved != 0) {
            onMoveListeners.forEach(listener -> listener.onMove(0, moved));
        }

        updatePowerUps(elapsedTime);
    }

    private void updatePowerUps(Duration elapsedTime) {
        shield.update(elapsedTime);

        boolean wasShrunk = shrink.isEnabled();
        shrink.update(elapsedTime);
        if (!shrink.isEnabled() && wasShrunk) {
            unshrink();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        shield.draw(canvas);
    }

    @Override
    public void onGlobalTouchEvent(MotionEvent event) {
        super.onGlobalTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            velocity = up;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            velocity = down;
        }
    }

    @Override
    public void onCollision(@NonNull CollidingGameObject other, Point point) {
        super.onCollision(other, point);

        if (other instanceof Obstacle) {
            onObstacleCollision();
        } else if (other instanceof PowerUpView powerUpView) {
            onPowerUpCollision(powerUpView.powerUp);
        }
    }

    private void onObstacleCollision() {
        if (shield.isEnabled()) {
            sounds.playSound(R.raw.no_power_up);
            shield.disable();
        } else {
            sounds.playSound(R.raw.death);
            destroy();
        }
    }

    private void onPowerUpCollision(PowerUp powerUp) {
        sounds.playSound(powerUp.audioId);
        switch (powerUp) {
            case Shield -> shield.enable();
            case Shrink -> shrink();
        }
    }

    private void shrink() {
        if (!shrink.isEnabled()) {
            rect.size.multiply(1 / SHRINK_FACTOR);
            createFrameBitmap();
            shield.createSprite();
        }

        shrink.enable();
    }

    private void unshrink() {
        rect.size.multiply(SHRINK_FACTOR);
        createFrameBitmap();
        shield.createSprite();

        shrink.disable();
    }

    public void addOnMoveListener(OnMoveListener listener) {
        onMoveListeners.add(listener);
    }

    @FunctionalInterface
    public interface OnMoveListener {
        void onMove(float x, float y);
    }
}

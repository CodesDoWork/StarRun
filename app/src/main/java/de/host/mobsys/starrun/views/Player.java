package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
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
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.base.views.BitmapObject;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.control.Sounds;
import de.host.mobsys.starrun.models.Difficulty;
import de.host.mobsys.starrun.models.PowerUp;

public class Player extends BitmapObject {
    private static final float UP_SPEED = 70;
    private static final float DOWN_SPEED = 40;
    private static final Duration SHIELD_DURATION = Duration.ofSeconds(10);
    private static final Duration SHRINK_DURATION = Duration.ofSeconds(10);
    private static final float SHRINK_FACTOR = 2;

    private final List<OnMoveListener> onMoveListeners = new ArrayList<>();
    private final Sounds sounds;

    private final Bitmap originalShieldSprite;
    private Bitmap shieldSprite;

    private Velocity down = new VelocityBuilder().down(DOWN_SPEED).build();
    private Velocity up = new VelocityBuilder().up(UP_SPEED).build();

    private Duration remainingShieldDuration = Duration.ZERO;
    private Duration remainingShrinkDuration = Duration.ZERO;
    private boolean isAnimationPlaying = false;

    public Player(Rect rect, Assets assets, Difficulty difficulty, Sounds sounds) {
        super(rect, assets.getPlayerBitmap());
        this.sounds = sounds;
        velocity = down;

        this.originalShieldSprite = assets.getShieldBitmap();
        createShieldSprite();

        difficulty.addChangeListener(value -> {
            down = new VelocityBuilder().down(DOWN_SPEED * difficulty.getHalf()).build();
            up = new VelocityBuilder().up(UP_SPEED * difficulty.getHalf()).build();
        });
    }

    public void setAnimationPlaying(boolean animationPlaying) {
        isAnimationPlaying = animationPlaying;
        if (animationPlaying) {
            velocity = Velocity.ZERO;
        }
    }

    private boolean hasShield() {
        return !(remainingShieldDuration.isNegative() || remainingShieldDuration.isZero());
    }

    private boolean isShrunk() {
        return !(remainingShrinkDuration.isNegative() || remainingShieldDuration.isZero());
    }

    @Override
    public void update(Duration elapsedTime) {
        if (!isAnimationPlaying) {
            float y = rect.getTop();
            super.update(elapsedTime);
            rect.ensureInScreen();

            float moved = rect.getTop() - y;
            if (moved != 0) {
                onMoveListeners.forEach(listener -> listener.onMove(0, moved));
            }
        }

        boolean wasShrunk = isShrunk();

        remainingShieldDuration = remainingShieldDuration.minus(elapsedTime);
        remainingShrinkDuration = remainingShrinkDuration.minus(elapsedTime);

        if (!isShrunk() && wasShrunk) {
            rect.size.multiply(SHRINK_FACTOR);
            createSprite();
            createShieldSprite();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (hasShield()) {
            canvas.drawBitmap(shieldSprite, rect.getMatrix(), null);
        }
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
            if (hasShield()) {
                sounds.playSound(R.raw.no_power_up);
                remainingShieldDuration = Duration.ZERO;
            } else {
                sounds.playSound(R.raw.death);
                destroy();
            }
        } else if (other instanceof PowerUpView powerUpView) {
            PowerUp powerUp = powerUpView.powerUp;
            sounds.playSound(powerUp.audioId);
            if (powerUp == PowerUp.Shield) {
                remainingShieldDuration = SHIELD_DURATION;
            } else if (powerUp == PowerUp.Shrink) {
                if (!isShrunk()) {
                    rect.size.multiply(1 / SHRINK_FACTOR);
                    createSprite();
                    createShieldSprite();
                }

                remainingShrinkDuration = SHRINK_DURATION;
            }
        }
    }

    private void createShieldSprite() {
        shieldSprite = BitmapUtils.scaleBitmap(
            originalShieldSprite,
            Size.squareFromWidth(rect.size.getX())
        );
    }

    public void addOnMoveListener(OnMoveListener listener) {
        onMoveListeners.add(listener);
    }

    @FunctionalInterface
    public interface OnMoveListener {
        void onMove(float x, float y);
    }
}

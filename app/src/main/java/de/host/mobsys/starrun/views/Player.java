package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.base.CollidingGameObject;
import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.views.BitmapObject;

public class Player extends BitmapObject {
    private static final Velocity DOWN = new VelocityBuilder().down(35).build();
    private static final Velocity UP = new VelocityBuilder().up(60).build();

    private final List<OnMoveListener> onMoveListeners = new ArrayList<>();
    private final List<OnCollisionListener> onCollisionListeners = new ArrayList<>();

    public Player(Rect rect, Bitmap sprite) {
        super(rect, sprite);
        velocity = DOWN;
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
    }

    @Override
    public void onGlobalTouchEvent(MotionEvent event) {
        super.onGlobalTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            velocity = UP;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            velocity = DOWN;
        }
    }

    @Override
    public void onCollision(@NonNull CollidingGameObject other, Point point) {
        onCollisionListeners.forEach(OnCollisionListener::onCollision);
    }

    public void addOnMoveListener(OnMoveListener listener) {
        onMoveListeners.add(listener);
    }

    public void addOnCollisionListener(OnCollisionListener listener) {
        onCollisionListeners.add(listener);
    }

    @FunctionalInterface
    public interface OnMoveListener {
        void onMove(float x, float y);
    }

    @FunctionalInterface
    public interface OnCollisionListener {
        void onCollision();
    }
}

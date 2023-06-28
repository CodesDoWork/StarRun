package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.view.MotionEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.views.BitmapObject;
import de.host.mobsys.starrun.models.Difficulty;

public class Player extends BitmapObject {
    private static final float UP_SPEED = 60;
    private static final float DOWN_SPEED = 35;

    private final List<OnMoveListener> onMoveListeners = new ArrayList<>();

    private Velocity down = new VelocityBuilder().down(DOWN_SPEED).build();
    private Velocity up = new VelocityBuilder().up(UP_SPEED).build();

    public Player(Rect rect, Bitmap sprite, Difficulty difficulty) {
        super(rect, sprite);
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

    public void addOnMoveListener(OnMoveListener listener) {
        onMoveListeners.add(listener);
    }

    @FunctionalInterface
    public interface OnMoveListener {
        void onMove(float x, float y);
    }
}

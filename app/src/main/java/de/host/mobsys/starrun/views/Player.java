package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.view.MotionEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.Rect;

public class Player extends BitmapObject {
    private static final Velocity DOWN = new VelocityBuilder().down(35).build();
    private static final Velocity UP = new VelocityBuilder().up(60).build();

    private final List<OnMoveListener> onMoveListeners = new ArrayList<>();

    public Player(Rect rect, Bitmap sprite) {
        super(rect, DOWN, sprite);
    }

    @Override
    public void update(Duration frameDuration) {
        float y = rect.getTop();
        super.update(frameDuration);
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

    public void addOnMoveListener(OnMoveListener listener) {
        onMoveListeners.add(listener);
    }

    public interface OnMoveListener {
        void onMove(float x, float y);
    }
}

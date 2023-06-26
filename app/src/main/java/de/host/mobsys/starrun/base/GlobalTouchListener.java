package de.host.mobsys.starrun.base;

import android.view.MotionEvent;

/**
 * Listener to handle global touch events of the app.
 */
@FunctionalInterface
public interface GlobalTouchListener {
    /**
     * Called when a touch event is received anywhere.
     */
    void onGlobalTouchEvent(MotionEvent event);
}

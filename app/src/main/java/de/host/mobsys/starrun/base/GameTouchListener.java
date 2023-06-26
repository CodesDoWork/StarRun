package de.host.mobsys.starrun.base;

import android.view.MotionEvent;

/**
 * Listener to handle touch events of the app.
 */
public interface GameTouchListener {
    /**
     * Called when a touch event is received anywhere.
     */
    void onGlobalTouchEvent(MotionEvent event);

    /**
     * Called when a touch event on the view is received.
     */
    void onTouchEvent(MotionEvent event);
}

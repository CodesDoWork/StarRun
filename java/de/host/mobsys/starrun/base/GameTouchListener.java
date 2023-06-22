package de.host.mobsys.starrun.base;

import android.view.MotionEvent;

public interface GameTouchListener {
    void onGlobalTouchEvent(MotionEvent event);
    void onTouchEvent(MotionEvent event);
}

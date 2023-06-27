package de.host.mobsys.starrun.base.views;

import android.graphics.Bitmap;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.base.size.Rect;

public class Button extends BitmapObject {

    private final List<OnClickListener> onClickListeners = new ArrayList<>();

    public Button(Rect rect, Bitmap sprite) {
        super(rect, sprite);
    }

    @Override
    public boolean containsCoordinates(int x, int y) {
        return rect.containsCoordinates(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            onClickListeners.forEach(OnClickListener::onClick);
        }

        return true;
    }

    public void addOnClickListener(OnClickListener listener) {
        onClickListeners.add(listener);
    }

    @FunctionalInterface
    public interface OnClickListener {
        void onClick();
    }
}

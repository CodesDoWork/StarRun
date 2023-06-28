package de.host.mobsys.starrun.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Difficulty {

    private final List<OnChangeListener> onChangeListeners = new ArrayList<>();

    private float value = 1;

    public void setFromScore(int score) {
        value = (float) (1 + Math.log(1 + score) / 2);
        Log.d("Difficulty", "setFromScore: " + value);
        onChangeListeners.forEach(listener -> listener.onChange(value));
    }

    public float get() {
        return value;
    }

    public float getHalf() {
        // add 0.5f because the base 1 is also halved.
        return value / 2 + 0.5f;
    }

    public void addChangeListener(OnChangeListener listener) {
        onChangeListeners.add(listener);
    }

    @FunctionalInterface
    public interface OnChangeListener {
        void onChange(float value);
    }
}

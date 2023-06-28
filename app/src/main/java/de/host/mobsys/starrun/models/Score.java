package de.host.mobsys.starrun.models;

import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.control.PreferenceInfo;
import de.host.mobsys.starrun.control.PreferenceStorage;

public class Score {

    private final List<OnChangeListener> onChangeListeners = new ArrayList<>();
    private final PreferenceStorage storage;

    private int score = 0;
    private int highScore;

    public Score(PreferenceStorage storage) {
        this.storage = storage;
        highScore = storage.get(PreferenceInfo.HIGHSCORE);

    }

    public void increment() {
        ++score;
        onChangeListeners.forEach(listener -> listener.onChange(score));
    }

    public void save() {
        if (score > highScore) {
            highScore = score;
            storage.set(PreferenceInfo.HIGHSCORE, highScore);
        }
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public void addChangeListener(OnChangeListener listener) {
        onChangeListeners.add(listener);
    }

    @FunctionalInterface
    public interface OnChangeListener {
        void onChange(int score);
    }
}

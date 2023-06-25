package de.host.mobsys.starrun.control;

import android.content.Context;

/**
 * A class to save the score and high score of the player
 */
public class ScorePreference {

    private final PreferenceStorage preferenceStorage;

    public ScorePreference(Context context) {
        preferenceStorage = new PreferenceStorage(context);
    }

    public int getHighScore() {
        return preferenceStorage.get(new PreferenceInfo<>("high_score", Integer.class, 0));
    }

    public void setHighScore(int highScore) {
        preferenceStorage.set(new PreferenceInfo<>("high_score", Integer.class), highScore);
    }

    //Maybe we need these to inform the player they beat the high score
    public int getScore() {
        return preferenceStorage.get(new PreferenceInfo<>("score", Integer.class, 0));
    }

    public void setScore(int score) {
        preferenceStorage.set(new PreferenceInfo<>("score", Integer.class), score);
    }
}


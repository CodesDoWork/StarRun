package de.host.mobsys.starrun.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;

import java.time.Duration;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.control.PreferenceStorage;
import de.host.mobsys.starrun.control.ScorePreference;

/**
* The ScoreObject class presents the score at the game. It is based on a time input. The score increases
* by 1, after one second. It also stores the highest score achieved as the high score.
 */
public class ScoreObject extends TextObject {
    private int score = 0;
    private int highScore = 0;
    private Duration elapsedTime = Duration.ZERO;
    private ScorePreference scorePreference;

    public ScoreObject(Rect rect, Context context) {
        super(rect, "", Color.WHITE);
        scorePreference = new ScorePreference(context);
        highScore = scorePreference.getHighScore();
    }

    @Override
    public void update(Duration frameDuration) {
        elapsedTime = elapsedTime.plus(frameDuration);
        if (elapsedTime.getSeconds() >= 1) {
            score++;
            elapsedTime = elapsedTime.minusSeconds(1);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        text = "Score: " + score + "\nHigh-Score: " + highScore;
        super.draw(canvas);
    }

    public void saveHighScore() {
        if (score > highScore) {
            highScore = score;
            scorePreference.setHighScore(highScore);
        }
    }
}


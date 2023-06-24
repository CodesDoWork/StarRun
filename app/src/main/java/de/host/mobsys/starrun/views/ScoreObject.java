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

/*
The ScoreObject class presents the score at the game. It is based on a time input. The score increases
by 1, after a 1 second. It also stores the highest score achieved as the highscore.
 */
public class ScoreObject extends TextObject {
    private int score = 0;
    private int highScore = 0;
    private Duration elapsedTime = Duration.ZERO;
    private SharedPreferences sharedPreferences;

    public ScoreObject(Rect rect, Context context) {
        super(rect, "", Color.WHITE);
        sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("high_score", 0);
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
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("high_score", highScore);
            editor.apply();
        }
    }
}


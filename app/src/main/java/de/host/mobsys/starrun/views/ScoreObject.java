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
Die Klasse ScoreObject präsentiert den Score (Punktestand) beim Spiel. Es basiert auf eine Zeiteingabe.
Der Score erhöht sich um 1, nach einer 1 Sekunde. Außerdem speichert er den höchst erzielten Score als
Highscore.
 */
public class ScoreObject extends GameObject {
    private int score = 0;
    private int highScore = 0;
    private Duration elapsedTime = Duration.ZERO;
    private SharedPreferences sharedPreferences;

    public ScoreObject(Rect rect, Context context) {
        super(rect);
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
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(48f);

        float textX = rect.getLeftPx();
        float textY = rect.getTopPx() + paint.getTextSize();

        canvas.drawText("Score: " + score, textX, textY, paint);
        canvas.drawText("High-Score: " + highScore, textX, textY + paint.getTextSize(), paint);
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


package de.host.mobsys.starrun.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.time.Duration;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.size.Rect;

/*
Die Klasse ScoreObject präsentiert den Score (Punktestand) beim Spiel. Es basiert auf eine Zeiteingabe.
Der Score erhöht sich um 1, nach einer 1 Sekunde.
 */
public class ScoreObject extends GameObject {
    private int score = 0;
    private Duration elapsedTime = Duration.ZERO;

    public ScoreObject(Rect rect) {
        super(rect);
    }

    @Override
    public void update(Duration frameDuration) {
        // Hier kannst du die Score-Logik implementieren, um den Punktestand zu erhöhen
        // basierend auf der vergangenen Zeit oder anderen Kriterien.
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
    }
}


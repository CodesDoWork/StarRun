package de.host.mobsys.starrun.views;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.time.Duration;

import de.host.mobsys.starrun.R;
import de.host.mobsys.starrun.base.physics.Velocity1D;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.base.views.TextObject;
import de.host.mobsys.starrun.control.Sounds;

public class Countdown extends TextObject {

    private static final Velocity1D alphaShrinkSpeed = new Velocity1D(255);

    private final float initialX;
    private final float initialY;
    private final float initialTextSize;
    private final Velocity1D shrinkSpeed;
    private final Sounds sounds;

    private Duration remainingDuration = null;

    public Countdown(Position position, Paint paint, Sounds sounds) {
        super(position, paint);
        initialX = position.getX();
        initialY = position.getY();
        initialTextSize = paint.getTextSize();
        shrinkSpeed = new Velocity1D(initialTextSize / 4);
        this.sounds = sounds;
    }

    public void start(int secs) {
        remainingDuration = Duration.ofSeconds(secs);
    }

    @Override
    public void update(Duration elapsedTime) {
        super.update(elapsedTime);
        if (remainingDuration == null) {
            return;
        }

        remainingDuration = remainingDuration.minus(elapsedTime);
        if (remainingDuration.isZero() || remainingDuration.isNegative()) {
            sounds.playSound(R.raw.countdown_end);
            destroy();
        }

        String timerText = String.valueOf(remainingDuration.getSeconds() + 1);
        if (!text.equals(timerText)) {
            text = timerText;
            paint.setTextSize(initialTextSize);
            paint.setAlpha(255);
            sounds.playSound(R.raw.countdown);
        } else {
            paint.setTextSize(paint.getTextSize() - shrinkSpeed.getValue(elapsedTime));
            paint.setAlpha(Math.round(paint.getAlpha() - alphaShrinkSpeed.getValue(elapsedTime)));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        float textHeightPx = SizeSystem.getInstance().heightFromPx((int) paint.getTextSize());
        position.setX(initialX - getTextWidth() / 2);
        position.setY(initialY - textHeightPx / 2);

        super.draw(canvas);
    }
}

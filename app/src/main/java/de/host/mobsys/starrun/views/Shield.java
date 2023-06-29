package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.time.Duration;

import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;

/**
 * Class to handle player shield
 */
public class Shield {
    private static final Duration SHIELD_DURATION = Duration.ofSeconds(10);

    private final Bitmap originalSprite;
    private final Rect rect;

    private Bitmap sprite;
    private Duration remainingDuration = Duration.ZERO;

    public Shield(Bitmap originalSprite, Rect rect) {
        this.originalSprite = originalSprite;
        this.rect = rect;
        createSprite();
    }

    void update(Duration elapsedTime) {
        remainingDuration = remainingDuration.minus(elapsedTime);
    }

    void draw(Canvas canvas) {
        if (isEnabled()) {
            Matrix spriteMatrix = rect.getMatrix();
            spriteMatrix.preTranslate(0, -rect.size.getYPx() / 2f);
            canvas.drawBitmap(sprite, spriteMatrix, null);
        }
    }

    void enable() {
        remainingDuration = SHIELD_DURATION;
    }

    void disable() {
        remainingDuration = Duration.ZERO;
    }

    boolean isEnabled() {
        return !(remainingDuration.isNegative() || remainingDuration.isZero());
    }

    void createSprite() {
        sprite = BitmapUtils.scaleBitmap(originalSprite, Size.squareFromWidth(rect.size.getX()));
    }
}

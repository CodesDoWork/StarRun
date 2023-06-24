package de.host.mobsys.starrun.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.size.Rect;

public class TextObject extends GameObject {
    protected String text;
    protected Paint paint;

    public TextObject(Rect rect, String text, int color) {
        super(rect);
        this.text = text;

        paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(48f);
    }

    @Override
    public void draw(Canvas canvas) {
        float textX = rect.getLeftPx();
        float textY = rect.getTopPx() + paint.getTextSize();

        canvas.drawText(text, textX, textY, paint);
    }
}

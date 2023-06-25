package de.host.mobsys.starrun.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.StaticLayout;

import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.size.Rect;

public class TextObject extends GameObject {
    protected String text;
    protected Paint paint;
    protected float lineHeight;

    public TextObject(Rect rect, String text, int color) {
        super(rect);
        this.text = text;

        paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(48f);
        lineHeight = -paint.ascent() + paint.descent();
    }

    @Override
    public void draw(Canvas canvas) {
        float textX = rect.getLeftPx();
        float textY = rect.getTopPx() + paint.getTextSize();

        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            canvas.drawText(lines[i], textX, textY + i * paint.getTextSize(), paint);
        }
    }
}

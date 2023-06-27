package de.host.mobsys.starrun.base.views;

import android.graphics.Canvas;
import android.graphics.Paint;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.size.Position;

public class TextObject extends GameObject {

    private final Paint paint;
    private final float lineHeight;
    private final float textSize;

    private String text;

    public TextObject(Position position, Paint paint) {
        this(position, paint, "");
    }

    public TextObject(Position position, Paint paint, String text) {
        super(position);
        this.paint = paint;
        this.text = text;

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        lineHeight = fontMetrics.descent - fontMetrics.ascent;
        textSize = paint.getTextSize();
    }

    @Override
    public void draw(Canvas canvas) {
        float textX = position.getXPx();
        float textY = position.getYPx() + textSize;

        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; ++i) {
            canvas.drawText(lines[i], textX, textY + i * (textSize + lineHeight), paint);
        }
    }

    public void setText(String text) {
        this.text = text;
    }
}

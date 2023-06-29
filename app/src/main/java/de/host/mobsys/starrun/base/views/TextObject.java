package de.host.mobsys.starrun.base.views;

import android.graphics.Canvas;
import android.graphics.Paint;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.SizeSystem;

/**
 * A GameObject to display text
 */
public class TextObject extends GameObject {

    protected final Paint paint;

    protected String text;

    public TextObject(Position position, Paint paint) {
        this(position, paint, "");
    }

    public TextObject(Position position, Paint paint, String text) {
        super(position);
        this.paint = paint;
        this.text = text;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float lineHeight = fontMetrics.descent - fontMetrics.ascent;
        float textSize = paint.getTextSize();
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

    protected float getTextWidth() {
        float[] widths = new float[text.length()];
        paint.getTextWidths(text, widths);
        float totalWidth = 0;
        for (float width : widths) {
            totalWidth += width;
        }

        return SizeSystem.getInstance().widthFromPx(Math.round(totalWidth));
    }
}

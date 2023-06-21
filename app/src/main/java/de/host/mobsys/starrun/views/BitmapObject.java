package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Rect;

public class BitmapObject extends GameObject {

    protected final Bitmap sprite;

    public BitmapObject(Rect rect, Bitmap sprite) {
        this(rect, Velocity.ZERO, sprite);
    }

    public BitmapObject(Rect rect, Velocity velocity, Bitmap sprite) {
        super(rect, velocity);
        this.sprite = BitmapUtils.scaleBitmap(sprite, rect.size);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(sprite, rect.position.getMatrix(), null);
    }
}
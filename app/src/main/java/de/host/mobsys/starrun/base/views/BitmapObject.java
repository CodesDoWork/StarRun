package de.host.mobsys.starrun.base.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import java.time.Duration;

import de.host.mobsys.starrun.base.CollidingGameObject;
import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.physics.Velocity1D;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.SizeSystem;

/**
 * A GameObject drawing a bitmap to the screen.
 */
public class BitmapObject extends GameObject implements CollidingGameObject {

    protected final Rect rect;
    protected final Bitmap sprite;
    private final Bitmap fullscreenBitmap;
    private final Canvas objectOnly;

    protected Velocity1D rotationSpeed = Velocity1D.ZERO;

    public BitmapObject(Rect rect, Bitmap sprite) {
        super(rect.position);
        this.rect = rect;
        this.sprite = BitmapUtils.scaleBitmap(sprite, rect.size);
        fullscreenBitmap = Bitmap.createBitmap(
            SizeSystem.getDisplayWidth(),
            SizeSystem.getDisplayHeight(),
            Bitmap.Config.ARGB_8888
        );
        objectOnly = new Canvas(fullscreenBitmap);
    }

    @Override
    public void update(Duration elapsedTime) {
        super.update(elapsedTime);
        rect.rotate(rotationSpeed.getValue(elapsedTime));
    }

    @Override
    public void draw(Canvas canvas) {
        objectOnly.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        objectOnly.drawBitmap(sprite, rect.getMatrix(), null);
        canvas.drawBitmap(sprite, rect.getMatrix(), null);
    }

    @Override
    public boolean containsCoordinates(int x, int y) {
        if (x < 0
            || x >= fullscreenBitmap.getWidth()
            || y < 0
            || y >= fullscreenBitmap.getHeight()) {
            return false;
        }

        return fullscreenBitmap.getPixel(x, y) != Color.TRANSPARENT;
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    public void setRotation(float rotation) {
        rect.setRotation(rotation);
    }
}
package de.host.mobsys.starrun.base.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
    private final Bitmap fullscreenBitmap;
    private final Canvas objectOnly;
    private final List<OnCollisionListener> onCollisionListeners = new ArrayList<>();
    protected Bitmap originalSprite;
    protected Bitmap sprite;
    protected Velocity1D rotationSpeed = Velocity1D.ZERO;

    public BitmapObject(Rect rect, Bitmap sprite) {
        super(rect.position);
        this.rect = rect;
        this.originalSprite = sprite;
        createSprite();
        fullscreenBitmap = Bitmap.createBitmap(
            SizeSystem.getDisplayWidth(),
            SizeSystem.getDisplayHeight(),
            Bitmap.Config.ARGB_8888
        );
        objectOnly = new Canvas(fullscreenBitmap);
    }

    protected void createSprite() {
        sprite = BitmapUtils.scaleBitmap(originalSprite, rect.size);
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

    @Override
    public void onCollision(@NonNull CollidingGameObject other, Point point) {
        onCollisionListeners.forEach(listener -> listener.onCollision(other, point));
    }

    public void addOnCollisionListener(OnCollisionListener listener) {
        onCollisionListeners.add(listener);
    }

    @FunctionalInterface
    public interface OnCollisionListener {
        void onCollision(@NonNull CollidingGameObject other, Point point);
    }
}
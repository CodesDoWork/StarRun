package de.host.mobsys.starrun.base.size;

import android.graphics.Matrix;

/**
 * Rectangular bounds for a view with a position, a size, and a rotation.
 */
public class Rect {

    public final Position position;
    public final Size size;
    private float rotation;

    public Rect(Position position, Size size, float rotation) {
        this.position = position;
        this.size = size;
        this.rotation = rotation;
    }

    public Rect(Position position, Size size) {
        this(position, size, 0);
    }

    public float getRotation() {
        return rotation;
    }

    public void rotate(float angle) {
        rotation += angle;
    }

    public float getLeft() {
        return position.x;
    }

    public float getRight() {
        return position.x + size.x;
    }

    public float getTop() {
        return position.y;
    }

    public float getBottom() {
        return position.y + size.y;
    }

    public int getLeftPx() {
        return position.getXPx();
    }

    public int getRightPx() {
        return position.getXPx() + size.getWidthPx();
    }

    public int getTopPx() {
        return position.getYPx();
    }

    public int getBottomPx() {
        return position.getYPx() + size.getHeightPx();
    }

    public void translate(float x, float y) {
        position.translate(x, y);
    }

    public Matrix getMatrix() {
        Matrix matrix = position.getMatrix();
        matrix.preRotate(rotation, size.getWidthPx() / 2f, size.getHeightPx() / 2f);

        return matrix;
    }

    public boolean contains(float x, float y) {
        return x >= position.getXPx()
               && x <= position.getXPx() + size.getWidthPx()
               && y >= position.getYPx()
               && y <= position.getYPx() + size.getHeightPx();
    }

    public boolean collidesWith(Rect other) {
        return other.getLeftPx() <= getRightPx()
               && other.getRightPx() >= getLeftPx()
               && other.getTopPx() <= getBottomPx()
               && other.getBottomPx() >= getTopPx();
    }

    public void ensureInScreen() {
        if (getLeftPx() < 0) {
            position.setXPx(0);
        }
        if (getRightPx() > SizeSystem.getDisplayWidth()) {
            position.setXPx(SizeSystem.getDisplayWidth() - size.getWidthPx());
        }
        if (getTopPx() < 0) {
            position.setYPx(0);
        }
        if (getBottomPx() > SizeSystem.getDisplayHeight()) {
            position.setYPx(SizeSystem.getDisplayHeight() - size.getHeightPx());
        }
    }
}

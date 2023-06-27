package de.host.mobsys.starrun.base.size;

import android.graphics.Matrix;
import android.graphics.Point;

import de.host.mobsys.starrun.base.collision.IntersectionCalculator;

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

    public void rotate(float angle) {
        rotation += angle;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
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

    public Matrix getMatrix() {
        Matrix matrix = position.getMatrix();
        matrix.preRotate(rotation, size.getWidthPx() / 2f, size.getHeightPx() / 2f);

        return matrix;
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

    public boolean containsCoordinates(int x, int y) {
        return getLeftPx() <= x && x <= getRightPx() && getTopPx() <= y && y <= getBottomPx();
    }

    public Point[] intersectPx(Rect rect) {
        return IntersectionCalculator.computeIntersection(toPointsPx(), rect.toPointsPx());
    }

    private Point[] toPointsPx() {
        float halfWidth = size.getWidthPx() / 2f;
        float halfHeight = size.getHeightPx() / 2f;

        float centerX = getLeftPx() + halfWidth;
        float centerY = getTopPx() + halfHeight;

        double angleRad = Math.toRadians(rotation);
        double sin = Math.sin(angleRad);
        double cos = Math.cos(angleRad);

        int x1 = (int) Math.round(centerX + (cos * -halfWidth) - (sin * -halfHeight));
        int y1 = (int) Math.round(centerY + (sin * -halfWidth) + (cos * -halfHeight));
        Point p1 = new Point(x1, y1);

        int x2 = (int) Math.round(centerX + (cos * halfWidth) - (sin * -halfHeight));
        int y2 = (int) Math.round(centerY + (sin * halfWidth) + (cos * -halfHeight));
        Point p2 = new Point(x2, y2);

        int x3 = (int) Math.round(centerX + (cos * halfWidth) - (sin * halfHeight));
        int y3 = (int) Math.round(centerY + (sin * halfWidth) + (cos * halfHeight));
        Point p3 = new Point(x3, y3);

        int x4 = (int) Math.round(centerX + (cos * -halfWidth) - (sin * halfHeight));
        int y4 = (int) Math.round(centerY + (sin * -halfWidth) + (cos * halfHeight));
        Point p4 = new Point(x4, y4);

        return new Point[]{ p1, p2, p3, p4 };
    }
}

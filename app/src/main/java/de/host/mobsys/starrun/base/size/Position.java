package de.host.mobsys.starrun.base.size;

import android.graphics.Matrix;

/**
 * Represents a position as 2D vector with x and y coordinates notated in units of the SizeSystem set.
 */
public class Position extends SizeVector2D {

    public Position(float x, float y) {
        super(x, y);
    }

    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public Matrix getMatrix() {
        Matrix matrix = new Matrix();
        matrix.preTranslate(getXPx(), getYPx());
        return matrix;
    }
}

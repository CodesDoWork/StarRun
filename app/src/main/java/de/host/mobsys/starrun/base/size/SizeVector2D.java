package de.host.mobsys.starrun.base.size;

/**
 * 2D vector with x and v values in units of the SizeSystem set.
 */
public class SizeVector2D {

    protected float x;
    protected float y;

    public SizeVector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getXPx() {
        return SizeSystem.getInstance().widthToPx(x);
    }

    public void setXPx(int px) {
        this.x = SizeSystem.getInstance().widthFromPx(px);
    }

    public int getYPx() {
        return SizeSystem.getInstance().heightToPx(y);
    }

    public void setYPx(int px) {
        this.y = SizeSystem.getInstance().heightFromPx(px);
    }
}

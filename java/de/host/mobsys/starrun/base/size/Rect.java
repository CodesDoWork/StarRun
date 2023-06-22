package de.host.mobsys.starrun.base.size;

public class Rect {

    public final Position position;
    public final Size size;

    public Rect(Position position, Size size) {
        this.position = position;
        this.size = size;
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

    public boolean isOutOfScreen() {
        return getRightPx() < 0
               || getBottomPx() < 0
               || getLeftPx() > SizeSystem.getDisplayWidth()
               || getTopPx() > SizeSystem.getDisplayHeight();
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

package de.host.mobsys.starrun.base.size;

/**
 * Represents a rectangular size as 2D vector with x (width) and y (height) values notated in units
 * of the SizeSystem set.
 */
public class Size extends SizeVector2D {

    private Size(float width, float height) {
        super(width, height);
    }

    public static Size squareFromWidth(float width) {
        return new Size(width, SizeSystem.getInstance().getHeightForSquareFromWidth(width));
    }

    public static Size squareFromHeight(float height) {
        return new Size(SizeSystem.getInstance().getWidthForSquareFromHeight(height), height);
    }

    public static Size fromWidthAndHeight(float width, float height) {
        return new Size(width, height);
    }

    public static Size fromWidthAndAspectRatio(float width, float ratio) {
        return new Size(
            width,
            SizeSystem.getInstance().getHeightForSquareFromWidth(width) / ratio
        );
    }

    public static Size fromHeightAndAspectRatio(float height, float ratio) {
        return new Size(
            SizeSystem.getInstance().getWidthForSquareFromHeight(height) * ratio,
            height
        );
    }

    public int getWidthPx() {
        return getXPx();
    }

    public int getHeightPx() {
        return getYPx();
    }
}

package de.host.mobsys.starrun.base.size;

/**
 * Class to convert values of different sizing systems to pixels.
 */
public abstract class SizeSystem {

    private static SizeSystem sizeSystem;

    private static int displayWidth;
    private static int displayHeight;
    private static float displayRatio;

    public static void setup(int displayWidthPx, int displayHeightPx) {
        displayWidth = displayWidthPx;
        displayHeight = displayHeightPx;
        displayRatio = (float) displayWidth / displayHeight;
    }

    public static SizeSystem getInstance() {
        return sizeSystem;
    }

    public static void setSizeSystem(SizeSystem sizeSystem) {
        SizeSystem.sizeSystem = sizeSystem;
    }

    public static int getDisplayWidth() {
        return displayWidth;
    }

    public static int getDisplayHeight() {
        return displayHeight;
    }

    public static float getDisplayRatio() {
        return displayRatio;
    }

    public abstract int widthToPx(float width);

    public abstract float widthFromPx(int widthPx);

    public abstract int heightToPx(float height);

    public abstract float heightFromPx(int heightPx);

    public abstract float getHeightForSquareFromWidth(float width);

    public abstract float getWidthForSquareFromHeight(float height);
}

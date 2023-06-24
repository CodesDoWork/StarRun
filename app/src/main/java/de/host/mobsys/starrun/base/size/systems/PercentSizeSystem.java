package de.host.mobsys.starrun.base.size.systems;

import de.host.mobsys.starrun.base.size.SizeSystem;

/**
 * Scales sizes as percentages (0 to 100) of the display width and height.
 */
public class PercentSizeSystem extends SizeSystem {

    @Override
    public int widthToPx(float width) {
        return Math.round(width / 100 * getDisplayWidth());
    }

    @Override
    public float widthFromPx(int widthPx) {
        return widthPx * 100f / getDisplayWidth();
    }

    @Override
    public int heightToPx(float height) {
        return Math.round(height / 100 * getDisplayHeight());
    }

    @Override
    public float heightFromPx(int heightPx) {
        return heightPx * 100f / getDisplayHeight();
    }

    @Override
    public float getHeightForSquareFromWidth(float width) {
        return width * getDisplayRatio();
    }

    @Override
    public float getWidthForSquareFromHeight(float height) {
        return height / getDisplayRatio();
    }
}

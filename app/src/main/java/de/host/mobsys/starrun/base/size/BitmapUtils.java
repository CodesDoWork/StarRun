package de.host.mobsys.starrun.base.size;

import android.graphics.Bitmap;

public abstract class BitmapUtils {

    public static Bitmap scaleBitmap(Bitmap bitmap, Size size) {
        return Bitmap.createScaledBitmap(
            bitmap,
            size.getWidthPx(),
            size.getHeightPx(),
            true
        );
    }

    public static Size getSizeByWidth(Bitmap bitmap, float width) {
        return Size.fromWidthAndAspectRatio(width, getAspectRatio(bitmap));
    }

    public static Size getSizeByHeight(Bitmap bitmap, float height) {
        return Size.fromHeightAndAspectRatio(height, getAspectRatio(bitmap));
    }

    public static float getAspectRatio(Bitmap bitmap) {
        return 1f * bitmap.getWidth() / bitmap.getHeight();
    }
}

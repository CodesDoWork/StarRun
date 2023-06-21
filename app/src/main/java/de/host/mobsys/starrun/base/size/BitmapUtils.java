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
}

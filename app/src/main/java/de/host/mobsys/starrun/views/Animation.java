package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.time.Duration;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;

/**
 * The Animation class extends the functions of the GameObject class and is used to display and
 * manage an animation. It manages a sprite sheet consisting of several frames and allows playing
 * the animation by updating and drawing the individual frames.
 */
public class Animation extends GameObject {
    private Bitmap spriteSheet;
    private int frameCount;
    private int currentFrame;
    private int frameWidth;
    private int frameHeight;
    private int frameDurationMillis;
    private long startTimeMillis;
    private Size scalingSize;
    private Bitmap currentFrameBitmap;

    /**
     * Creates a new animation with the specified parameters.
     *
     * @param rect                The limitations of the animation (position and size).
     * @param spriteSheet         The sprite sheet that contains the frames of the animation.
     * @param frameCount          The number of frames in the sprite sheet.
     * @param frameDurationMillis The duration of a single frame in milliseconds.
     * @param scalingSize         The size to which the frames should be scaled.
     */
    public Animation(Rect rect, Bitmap spriteSheet, int frameCount, int frameDurationMillis, Size scalingSize) {
        super(rect);
        this.spriteSheet = spriteSheet;
        this.frameCount = frameCount;
        this.frameDurationMillis = frameDurationMillis;
        this.scalingSize = scalingSize;
    }

    /**
     * Starts the animation by initializing the necessary properties.
     * This method should be called after creating the animation.
     */
    public void startAnimation() {
        frameWidth = spriteSheet.getWidth() / frameCount;
        frameHeight = spriteSheet.getHeight();
        startTimeMillis = System.currentTimeMillis() + 1000;
        currentFrame = 0;
    }

    @Override
    public void update(Duration frameDuration) {
        long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        if (elapsedTimeMillis > frameDurationMillis) {
            currentFrame++;
            startTimeMillis = System.currentTimeMillis();
        }

        if(currentFrame < frameCount) {
            currentFrameBitmap = createFrameBitmap(currentFrame);
        } else {
            // Animation finished
            destroy();
        }

    }

    /**
     * Creates a single frame bitmap for the specified frame index.
     *
     * @param frameIndex The index of the desired frame.
     * @return The generated frame bitmap or null if the index is invalid.
     */
    private Bitmap createFrameBitmap(int frameIndex) {
        int srcX = frameIndex * frameWidth;

        if (srcX + frameWidth <= spriteSheet.getWidth()) {
            Bitmap frameBitmap = Bitmap.createBitmap(spriteSheet, srcX, 0, frameWidth, frameHeight);
            Bitmap scaledBitmap = BitmapUtils.scaleBitmap(frameBitmap, scalingSize);

            Matrix matrix = rect.position.getMatrix();
            return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } else {
            return null;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(currentFrameBitmap, rect.position.getMatrix(), null);
    }
}


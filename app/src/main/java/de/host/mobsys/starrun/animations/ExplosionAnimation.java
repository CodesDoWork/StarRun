package de.host.mobsys.starrun.animations;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.time.Duration;

import de.host.mobsys.starrun.base.size.Rect;

/**
 * The ExplosionsAnimation class inherits the basic animation functionality from the Animation class
 * and extends it to display and manage an explosion.
 */
public class ExplosionAnimation extends Animation {

    private Bitmap spriteSheet;
    private int frameCount;
    private int currentFrame;
    private int frameWidth;
    private int frameHeight;
    private int frameDurationMillis;
    private long startTimeMillis;

    public ExplosionAnimation(Rect rect, Bitmap spriteSheet) {
        super(rect);
        this.spriteSheet = spriteSheet;
        //initializeAnimation();
    }

    // This method shows the animation, remove the comment characters above to show the animation
    private void initializeAnimation() {
        frameCount = 11; // Count of Frames in Sprite Sheet
        currentFrame = 0;
        frameWidth = spriteSheet.getWidth() / frameCount;
        frameHeight = spriteSheet.getHeight();
        frameDurationMillis = 200;
        startTimeMillis = System.currentTimeMillis() + 1000;
    }



    @Override
    public void update(Duration frameDuration) {
        super.update(frameDuration);

        long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        if (elapsedTimeMillis > frameDurationMillis) {
            currentFrame++;
            startTimeMillis = System.currentTimeMillis();
        }

        if (currentFrame >= frameCount) {
            // Animation finished
            destroy();
        }

    }

    @Override
    public void draw(Canvas canvas) {
        int srcX = currentFrame * frameWidth;

        Bitmap frameBitmap = Bitmap.createBitmap(
            spriteSheet,
            srcX,
            0,
            frameWidth,
            frameHeight
        );

        float scaleX = 2;
        float scaleY = 2;

        canvas.save();
        canvas.scale(scaleX, scaleY, rect.position.getXPx(), rect.position.getYPx()); //Enlarge Animation
        canvas.drawBitmap(frameBitmap, rect.position.getXPx(), rect.position.getYPx(), null);
        canvas.restore();
    }
}


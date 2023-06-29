package de.host.mobsys.starrun.base.views;

import android.graphics.Bitmap;

import java.time.Duration;

import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Rect;

/**
 * Class to draw a GameObject with an animation by a sprite sheet.
 */
public class SpriteSheetObject extends BitmapObject {

    private final Bitmap spriteSheet;
    private final int frameCount;
    private final int frameWidth;
    private final LoopPolicy loopPolicy;
    private final Duration targetFrameDuration;

    private Duration currentFrameDuration = Duration.ZERO;
    private int currentFrameIdx = 0;
    private boolean isCountingUp = true;

    public SpriteSheetObject(
        Rect rect,
        Bitmap spriteSheet,
        int frameCount,
        LoopPolicy loopPolicy,
        Duration duration
    ) {
        super(rect, spriteSheet);
        this.spriteSheet = spriteSheet;
        this.frameCount = frameCount;
        frameWidth = spriteSheet.getWidth() / frameCount;
        this.loopPolicy = loopPolicy;
        this.targetFrameDuration = duration.dividedBy(frameCount);

        createFrameBitmap();
    }

    @Override
    public void update(Duration elapsedTime) {
        super.update(elapsedTime);

        currentFrameDuration = currentFrameDuration.plus(elapsedTime);
        if (currentFrameDuration.compareTo(targetFrameDuration) >= 0) {
            updateFrame();
        }
    }

    private void updateFrame() {
        currentFrameDuration = currentFrameDuration.minus(targetFrameDuration);
        updateFrameIndex();
        createFrameBitmap();
    }

    private boolean needsToLoop() {
        return currentFrameIdx == frameCount || currentFrameIdx < 0;
    }

    private void loop() {
        switch (loopPolicy) {
            case Replay -> currentFrameIdx = 0;
            case Bounce -> {
                isCountingUp = !isCountingUp;
                updateFrameIndex();
            }
            case Destroy -> destroy();
        }
    }

    private void updateFrameIndex() {
        currentFrameIdx += isCountingUp ? 1 : -1;
        if (needsToLoop()) {
            loop();
        }
    }

    /**
     * Creates a single frame bitmap for the current frame index.
     */
    protected void createFrameBitmap() {
        int srcX = currentFrameIdx * frameWidth;
        if (srcX >= 0 && srcX + frameWidth <= spriteSheet.getWidth()) {
            sprite = Bitmap.createBitmap(spriteSheet, srcX, 0, frameWidth, spriteSheet.getHeight());
            sprite = BitmapUtils.scaleBitmap(sprite, rect.size);
        }
    }

    /**
     * How to loop the animation:<br/>
     * Replay - start from beginning<br/>
     * Bounce - go back and forth<br/>
     * Destroy - no looping, destroy when animation finished<br/>
     */
    public enum LoopPolicy {
        Replay,
        Bounce,
        Destroy
    }
}

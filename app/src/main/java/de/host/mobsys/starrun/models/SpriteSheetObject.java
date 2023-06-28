package de.host.mobsys.starrun.models;

import android.graphics.Bitmap;

import java.time.Duration;

import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.views.BitmapObject;

public class SpriteSheetObject extends BitmapObject {

    private final Bitmap spriteSheet;
    private final int frameCount;
    private final int frameWidth;
    private final LoopPolicy loopPolicy;
    private final Duration frameDuration;

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
        this.frameDuration = duration.dividedBy(frameCount);

        createFrameBitmap();
    }

    @Override
    public void update(Duration elapsedTime) {
        super.update(elapsedTime);

        currentFrameDuration = currentFrameDuration.plus(elapsedTime);
        if (currentFrameDuration.compareTo(frameDuration) >= 0) {
            currentFrameDuration = currentFrameDuration.minus(frameDuration);
            updateFrameIndex();
            if (currentFrameIdx == frameCount || currentFrameIdx < 0) {
                switch (loopPolicy) {
                    case Replay -> currentFrameIdx = 0;
                    case Bounce -> {
                        isCountingUp = !isCountingUp;
                        updateFrameIndex();
                    }
                    case Destroy -> destroy();
                }
            }

            createFrameBitmap();
        }
    }

    private void updateFrameIndex() {
        currentFrameIdx += isCountingUp ? 1 : -1;
    }

    /**
     * Creates a single frame bitmap for the current frame index.
     */
    protected void createFrameBitmap() {
        int srcX = currentFrameIdx * frameWidth;
        if (srcX + frameWidth <= spriteSheet.getWidth()) {
            sprite = Bitmap.createBitmap(spriteSheet, srcX, 0, frameWidth, spriteSheet.getHeight());
            sprite = BitmapUtils.scaleBitmap(sprite, rect.size);
        }
    }

    public enum LoopPolicy {
        Replay,
        Bounce,
        Destroy
    }
}

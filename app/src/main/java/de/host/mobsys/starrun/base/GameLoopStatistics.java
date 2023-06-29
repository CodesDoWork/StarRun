package de.host.mobsys.starrun.base;

import android.util.Log;

import java.time.Duration;

import de.host.mobsys.starrun.base.physics.DurationUtils;

/**
 * Used to measure performance of a GameLoop
 */
public class GameLoopStatistics {
    private static final String TAG = "GAME STATS";

    private float updates = 0;
    private float frames = 0;
    private Duration secondCounter = Duration.ZERO;

    public void onUpdate(Duration elapsedTime) {
        ++updates;
        secondCounter = secondCounter.plus(elapsedTime);
    }

    public void onDraw() {
        ++frames;
        log();
    }

    private void log() {
        if (DurationUtils.toSeconds(secondCounter) >= 1) {
            Log.d(TAG, "FPS: " + frames + ", UPS: " + updates);

            updates = 0;
            frames = 0;
            secondCounter = Duration.ZERO;
        }
    }
}

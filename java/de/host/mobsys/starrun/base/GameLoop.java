package de.host.mobsys.starrun.base;

import java.time.Duration;

class GameLoop {

    private final Duration targetFrameTime;
    private boolean isRunning = false;
    private long lastFrameNanos = 0;

    private GameLoop(Duration targetFrameTime) {
        this.targetFrameTime = targetFrameTime;
    }

    static GameLoop withoutFPSCap() {
        return new GameLoop(Duration.ZERO);
    }

    static GameLoop withFPSRate(int targetFPS) {
        if (targetFPS < 1) {
            return GameLoop.withoutFPSCap();
        }

        return new GameLoop(Duration.ofSeconds(1).dividedBy(targetFPS));
    }

    @SuppressWarnings("BusyWait")
    void start(OnUpdateListener onUpdateListener) {
        isRunning = true;
        while (isRunning) {
            Duration frameDuration = getDurationSinceLastFrame();

            lastFrameNanos = System.nanoTime();
            onUpdateListener.onUpdate(frameDuration);
            Duration elapsedTime = Duration.ofNanos(System.nanoTime() - lastFrameNanos);

            long waitTimeMillis = targetFrameTime.minus(elapsedTime).toMillis();
            if (waitTimeMillis > 0) {
                try {
                    Thread.sleep(waitTimeMillis);
                } catch (InterruptedException e) {
                    stop();
                    break;
                }
            }
        }
    }

    void stop() {
        isRunning = false;
    }

    private Duration getDurationSinceLastFrame() {
        return lastFrameNanos == 0
               ? Duration.ZERO
               : Duration.ofNanos(System.nanoTime() - lastFrameNanos);
    }

    @FunctionalInterface
    interface OnUpdateListener {
        void onUpdate(Duration frameDuration);
    }
}

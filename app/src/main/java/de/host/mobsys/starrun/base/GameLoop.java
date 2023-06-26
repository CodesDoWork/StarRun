package de.host.mobsys.starrun.base;

import java.time.Duration;

/**
 * Class handling game-loop-logic to trigger update events of a game at a given or free frame rate.
 */
class GameLoop {

    private final GameLoopStatistics stats = new GameLoopStatistics();
    private final Duration targetFrameTime;
    private boolean isRunning = false;

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

    void start(GameLoopListener gameLoopListener) {
        isRunning = true;
        long lastUpdateNanos = System.nanoTime();
        long lastFrameNanos = System.nanoTime();
        while (isRunning) {
            Duration elapsedTime = Duration.ofNanos(System.nanoTime() - lastUpdateNanos);
            gameLoopListener.update(elapsedTime);
            stats.onUpdate(elapsedTime);
            lastUpdateNanos = System.nanoTime();

            Duration elapsedFrameTime = Duration.ofNanos(System.nanoTime() - lastFrameNanos);
            if (targetFrameTime.compareTo(elapsedFrameTime) <= 0) {
                gameLoopListener.draw();
                stats.onDraw();
                lastFrameNanos = System.nanoTime();
            }
        }
    }

    void stop() {
        isRunning = false;
    }

    interface GameLoopListener {
        void update(Duration elapsedTime);

        void draw();
    }
}

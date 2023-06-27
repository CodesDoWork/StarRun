package de.host.mobsys.starrun.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * This class can be added as a view to an activity.
 * It runs a game loop and triggers update, draw and touch events on its GameLayers and therefore
 * their GameObjects.
 */
public class GameView extends SurfaceView {
    private final SurfaceHolder surfaceHolder;
    private final GameThread gameThread;
    private final GameLoop gameLoop;
    private final List<GameLayer> layers = new ArrayList<>();

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        gameThread = new GameThread();
        gameLoop = GameLoop.withFPSRate(60);

        setOnTouchListener(new GameTouchListener());
    }

    public void start() {
        gameThread.start();
    }

    public void stop() {
        gameLoop.stop();
    }

    public void add(GameLayer layer) {
        layers.add(layer);
    }

    private void update(Duration elapsedTime) {
        layers.forEach(layer -> layer.update(elapsedTime));
    }

    private void draw() {
        if (!surfaceHolder.getSurface().isValid()) {
            return;
        }

        Canvas canvas = surfaceHolder.lockCanvas();
        layers.forEach(layer -> layer.draw(canvas));
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private class GameThread extends Thread {
        @Override
        public void run() {
            gameLoop.start(new GameLoop.GameLoopListener() {
                @Override
                public void update(Duration elapsedTime) {
                    GameView.this.update(elapsedTime);
                }

                @Override
                public void draw() {
                    GameView.this.draw();
                }
            });
        }
    }

    private class GameTouchListener implements OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean isTouchConsumed = false;
            ListIterator<GameLayer> layersIterator = layers.listIterator(layers.size());
            while (layersIterator.hasPrevious()) {
                GameLayer layer = layersIterator.previous();
                layer.onGlobalTouchEvent(event);
                if (!isTouchConsumed) {
                    isTouchConsumed = layer.onTouchEvent(event);
                }
            }

            return true;
        }
    }
}

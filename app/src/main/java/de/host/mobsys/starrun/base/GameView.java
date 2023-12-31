package de.host.mobsys.starrun.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

/**
 * This class can be added as a view to an activity.
 * It runs a game loop and triggers update, draw and touch events on its GameLayers and therefore
 * their GameObjects.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private final SurfaceHolder surfaceHolder;
    private final GameLoop gameLoop;
    private final List<GameLayer> layers = new ArrayList<>();

    private Bitmap savedStateBitmap = null;

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        gameLoop = GameLoop.withFPSRate(60);

        setOnTouchListener(new GameTouchListener());
    }

    public void start() {
        new Thread(() -> gameLoop.start(new GameLoop.GameLoopListener() {
            @Override
            public void update(Duration elapsedTime) {
                GameView.this.update(elapsedTime);
            }

            @Override
            public void draw() {
                GameView.this.draw();
            }
        })).start();
    }

    public void stop() {
        gameLoop.stop();
    }

    public boolean isRunning() {
        return gameLoop.isRunning();
    }

    private void update(Duration elapsedTime) {
        layers.forEach(layer -> layer.update(elapsedTime));
    }

    private void draw() {
        if (!surfaceHolder.getSurface().isValid()) {
            return;
        }

        useCanvas(this::draw);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        layers.forEach(layer -> layer.draw(canvas));
    }

    public void add(GameLayer layer) {
        layers.add(layer);
    }

    public List<GameLayer> getLayers() {
        return new ArrayList<>(layers);
    }

    public void setLayerStatusIf(Predicate<GameLayer> predicate, GameLayer.Status status) {
        for (GameLayer layer : getLayers()) {
            if (predicate.test(layer)) {
                layer.setStatus(status);
            }
        }
    }

    public void saveState() {
        savedStateBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(savedStateBitmap);
        draw(canvas);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (savedStateBitmap != null) {
            useCanvas(canvas -> canvas.drawBitmap(savedStateBitmap, 0, 0, null));
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
    }

    private void useCanvas(UseCanvas useCanvas) {
        Canvas canvas = surfaceHolder.lockCanvas();
        useCanvas.useCanvas(canvas);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @FunctionalInterface
    private interface UseCanvas {
        void useCanvas(Canvas canvas);
    }

    private class GameTouchListener implements OnTouchListener {

        private Point touchStart = null;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // save touch start for later events
                touchStart = new Point((int) event.getX(), (int) event.getY());
            }

            boolean isTouchConsumed = false;
            ListIterator<GameLayer> layersIterator = layers.listIterator(layers.size());
            while (layersIterator.hasPrevious() && !isTouchConsumed) {
                GameLayer layer = layersIterator.previous();
                isTouchConsumed = layer.onTouchEvent(event, touchStart);
                if (!isTouchConsumed) {
                    layer.onGlobalTouchEvent(event);
                }
            }

            return true;
        }
    }
}

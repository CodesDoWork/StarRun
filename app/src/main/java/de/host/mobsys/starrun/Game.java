package de.host.mobsys.starrun;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;

import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.base.GameLayer;
import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.GameView;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.base.views.BitmapObject;
import de.host.mobsys.starrun.base.views.Button;
import de.host.mobsys.starrun.base.views.CollisionLayer;
import de.host.mobsys.starrun.base.views.SpriteSheetObject;
import de.host.mobsys.starrun.base.views.TextObject;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.control.Sounds;
import de.host.mobsys.starrun.models.Difficulty;
import de.host.mobsys.starrun.models.PowerUp;
import de.host.mobsys.starrun.models.Score;
import de.host.mobsys.starrun.views.Background;
import de.host.mobsys.starrun.views.Countdown;
import de.host.mobsys.starrun.views.Obstacle;
import de.host.mobsys.starrun.views.Player;
import de.host.mobsys.starrun.views.PowerUpView;

/**
 * Class that controls the actual game
 */
public class Game {
    private static final int BASE_POWER_UP_SPAWN_DELAY = 25_000;
    private static final int BASE_OBSTACLE_SPAWN_DELAY = 3_000;

    private final GameLayer backgroundLayer = new GameLayer();
    private final GameLayer collisionLayer = new CollisionLayer();
    private final GameLayer animationLayer = new GameLayer();
    private final GameLayer overlayLayer = new GameLayer();
    private final GameLayer countdownLayer = new GameLayer();

    private final List<OnGameOverListener> gameOverListeners = new ArrayList<>();
    private final Difficulty difficulty = new Difficulty();
    private final Handler handler = new Handler();
    private final Context context;
    private final Assets assets;
    private final Sounds sounds;
    private final Score score;
    private final GameView game;

    // needs to be set to avoid update on pause/resume
    private final int highscore;

    private OnOpenMenuListener openMenuListener = null;
    private TextObject scoreObject;
    private Player player;

    private boolean isMenuDisabled = false;

    public Game(@NonNull Context context, Sounds sounds, @NonNull Score score, int highscore) {
        this.context = context;
        assets = new Assets(context.getResources().getAssets());
        this.sounds = sounds;
        this.score = score;
        this.highscore = highscore;
        score.addChangeListener(difficulty::setFromScore);

        game = new GameView(context);
        setupGame();
    }

    public GameView getView() {
        return game;
    }

    private void setupGame() {
        game.add(backgroundLayer);
        game.add(collisionLayer);
        game.add(animationLayer);
        game.add(overlayLayer);
        game.add(countdownLayer);

        createBackground();
        createPlayer();
        createScore();
        createMenuButton();
    }

    private void createBackground() {
        backgroundLayer.add(new Background(110, assets, difficulty));
    }

    private void createPlayer() {
        Bitmap playerSprite = assets.getPlayerBitmap();

        Rect playerRect = new Rect(
            new Position(5, 45),
            BitmapUtils.getSizeByWidth(playerSprite, 15)
        );

        player = new Player(playerRect, assets, difficulty, sounds);
        player.addOnMoveListener((x, y) -> backgroundLayer.translate(0, -y / 100));
        player.addOnDestroyListener(this::gameOver);
        collisionLayer.add(player);
    }

    private void createPowerUps(boolean isFirst) {
        if (!isFirst) {
            PowerUpView powerUpView = PowerUpView.createRandom(assets, difficulty, sounds);
            collisionLayer.add(powerUpView);
            if (powerUpView.powerUp == PowerUp.Bomb) {
                handleBombPowerUp(powerUpView);
            }
        }

        if (game.isRunning()) {
            handler.postDelayed(
                () -> createPowerUps(false),
                (int) (BASE_POWER_UP_SPAWN_DELAY / difficulty.get())
            );
        }
    }

    private void handleBombPowerUp(@NonNull PowerUpView powerUpView) {
        powerUpView.addOnCollisionListener((other, p) -> {
            if (other instanceof Player) {
                collisionLayer.getGameObjects()
                              .stream()
                              .filter(obj -> obj instanceof Obstacle)
                              .forEach(obstacle -> explode((BitmapObject) obstacle));
            }
        });
    }

    private void createObstacles() {
        Obstacle obstacle = Obstacle.createRandom(assets, difficulty, sounds);
        obstacle.addOnCollisionListener((other, p) -> {
            if (other instanceof Obstacle otherObstacle) {
                animateObstacleCollision(obstacle, otherObstacle, p);
            } else if (other instanceof Player) {
                createExplosion(obstacle.getRect());
            }
        });
        obstacle.addOnDestroyListener(() -> {
            if (obstacle.getRect().getLeftPx() < 0) {
                incrementScore();
            }
        });
        collisionLayer.add(obstacle);

        if (game.isRunning()) {
            handler.postDelayed(
                this::createObstacles,
                (int) (BASE_OBSTACLE_SPAWN_DELAY / difficulty.get())
            );
        }
    }

    private void animateObstacleCollision(Obstacle obstacle, Obstacle other, Point p) {
        createExplosionAt(p);
        handler.postDelayed(() -> createExplosion(obstacle.getRect()), 150);
        handler.postDelayed(() -> createExplosion(other.getRect()), 300);
    }

    private void createScore() {
        Paint scorePaint = createPaint(SizeSystem.getInstance().heightToPx(2.75f));
        scoreObject = new TextObject(new Position(65, 5), scorePaint);
        overlayLayer.add(scoreObject);
        setScoreText();
    }

    private void incrementScore() {
        score.increment();
        setScoreText();
    }

    @SuppressLint("StringFormatInvalid")
    private void setScoreText() {
        String scoreText = context.getString(R.string.score, score.getScore());
        String highscoreText = context.getString(R.string.highscore, highscore);
        scoreObject.setText(scoreText + "\n" + highscoreText);
    }

    private void createMenuButton() {
        Position buttonPosition = new Position(90, 5);
        Size buttonSize = Size.squareFromWidth(5);
        Rect buttonRect = new Rect(buttonPosition, buttonSize);

        Button menuButton = new Button(buttonRect, assets.getPauseButtonBitmap());
        menuButton.addOnClickListener(() -> {
            pause();
            openMenuListener.onOpenMenu();
        });
        overlayLayer.add(menuButton);
    }

    private void countdown() {
        // clear countdown before to avoid inconsistencies
        countdownLayer.getGameObjects().forEach(GameObject::destroy);

        game.setLayerStatusIf(layer -> layer != countdownLayer, GameLayer.Status.DrawEnabled);
        isMenuDisabled = true;

        Countdown countdown = new Countdown(new Position(50, 50), createPaint(120), sounds);
        countdown.addOnDestroyListener(() -> {
            isMenuDisabled = false;
            game.setLayerStatusIf(layer -> layer != countdownLayer, GameLayer.Status.Enabled);
            if (!sounds.isMusicPlaying()) {
                sounds.playMusic(R.raw.game_music);
            }

            createObstacles();
            createPowerUps(true);
        });
        countdownLayer.add(countdown);
        countdown.start(3);
    }

    @NonNull
    private Paint createPaint(float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        paint.setTypeface(assets.readFont());
        paint.setColor(Color.WHITE);

        return paint;
    }

    private void explode(@NonNull BitmapObject obj) {
        obj.destroy();
        createExplosion(obj.getRect());
    }

    private void createExplosionAt(Point p) {
        float x = SizeSystem.getInstance().widthFromPx(p.x);
        float y = SizeSystem.getInstance().heightFromPx(p.y);
        Size size = Size.squareFromWidth(5);
        Position position = new Position(x - size.getX() / 2, y - size.getY() / 2);

        createExplosion(new Rect(position, size));
    }

    @NonNull
    private SpriteSheetObject createExplosion(Rect rect) {
        SpriteSheetObject explosion = new SpriteSheetObject(
            rect,
            assets.getExplosionAnimation(),
            12,
            SpriteSheetObject.LoopPolicy.Destroy,
            Duration.ofMillis(1000)
        );
        animationLayer.add(explosion);

        return explosion;
    }

    public void start() {
        game.start();
        countdown();
    }

    public void pause() {
        game.stop();
        game.saveState();
        score.save();
    }

    private void gameOver() {
        game.setLayerStatusIf(layer -> layer != animationLayer, GameLayer.Status.DrawEnabled);

        Rect playerRect = player.getRect();
        createExplosion(new Rect(
            playerRect.position,
            Size.squareFromWidth(playerRect.size.getX())
        )).addOnDestroyListener(() -> {
            sounds.pauseMusic();
            pause();
            gameOverListeners.forEach(OnGameOverListener::onGameOver);
        });
    }

    public boolean isMenuDisabled() {
        return isMenuDisabled;
    }

    public void setOpenMenuListener(OnOpenMenuListener openMenuListener) {
        this.openMenuListener = openMenuListener;
    }

    public void addGameOverListener(OnGameOverListener listener) {
        gameOverListeners.add(listener);
    }

    @FunctionalInterface
    interface OnOpenMenuListener {
        void onOpenMenu();
    }

    @FunctionalInterface
    interface OnGameOverListener {
        void onGameOver();
    }
}

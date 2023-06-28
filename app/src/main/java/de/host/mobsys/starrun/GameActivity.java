package de.host.mobsys.starrun;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.time.Duration;
import java.util.function.Predicate;

import de.host.mobsys.starrun.base.GameLayer;
import de.host.mobsys.starrun.base.GameView;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.base.size.systems.PercentSizeSystem;
import de.host.mobsys.starrun.base.views.Button;
import de.host.mobsys.starrun.base.views.CollisionLayer;
import de.host.mobsys.starrun.base.views.TextObject;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.control.PreferenceInfo;
import de.host.mobsys.starrun.control.Sounds;
import de.host.mobsys.starrun.databinding.GameOverBinding;
import de.host.mobsys.starrun.databinding.PauseMenuBinding;
import de.host.mobsys.starrun.models.Difficulty;
import de.host.mobsys.starrun.models.PowerUp;
import de.host.mobsys.starrun.models.Score;
import de.host.mobsys.starrun.models.SpriteSheetObject;
import de.host.mobsys.starrun.views.Background;
import de.host.mobsys.starrun.views.Countdown;
import de.host.mobsys.starrun.views.Obstacle;
import de.host.mobsys.starrun.views.Player;
import de.host.mobsys.starrun.views.PowerUpView;

public class GameActivity extends BaseActivity {

    private final GameLayer backgroundLayer = new GameLayer();
    private final GameLayer collisionLayer = new CollisionLayer();
    private final GameLayer animationLayer = new GameLayer();
    private final GameLayer overlayLayer = new GameLayer();
    private final GameLayer countdownLayer = new GameLayer();

    private final Handler handler = new Handler();
    private final Difficulty difficulty = new Difficulty();

    private GameView game;
    private Assets assets;
    private Sounds sounds;

    private Score score;
    private int highScore = 0;

    private AlertDialog menu = null;
    private AlertDialog gameOverMenu = null;
    private TextObject scoreObject;

    private boolean isRecreating = false;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRecreating = false;

        assets = new Assets(getResources().getAssets());
        sounds = new Sounds(this);

        score = new Score(storage);
        score.addChangeListener(difficulty::setFromScore);

        // only update if unset
        if (highScore == 0) {
            highScore = storage.get(PreferenceInfo.HIGHSCORE);
        }

        setupSizeSystem();
        createMenuDialog();
        createGameOverDialog();
        setupGame();
        startGame();
    }

    private void setupSizeSystem() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);

        SizeSystem.setup(size.x, Math.max(size.y, realSize.y));
        SizeSystem.setSizeSystem(new PercentSizeSystem());
    }

    private void setupGame() {
        game = new GameView(this);
        setContentView(game);

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
        Background background = new Background(110, assets, difficulty);
        backgroundLayer.add(background);
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
                powerUpView.addOnCollisionListener((other, p) -> {
                    if (other instanceof Player) {
                        collisionLayer.getGameObjects()
                                      .stream()
                                      .filter(obj -> obj instanceof Obstacle)
                                      .forEach(obstacle -> {
                                          obstacle.destroy();
                                          createExplosion(((Obstacle) obstacle).getRect());
                                      });
                    }
                });
            }
        }

        if (game.isRunning()) {
            handler.postDelayed(() -> createPowerUps(false), (int) (25_000 / difficulty.get()));
        }
    }

    private void createObstacles() {
        Obstacle obstacle = Obstacle.createRandom(assets, difficulty, sounds);
        obstacle.addOnCollisionListener((other, p) -> {
            if (other instanceof Obstacle) {
                float x = SizeSystem.getInstance().widthFromPx(p.x);
                float y = SizeSystem.getInstance().heightFromPx(p.y);
                Size size = Size.squareFromWidth(5);
                Position position = new Position(x - size.getX() / 2, y - size.getY() / 2);

                createExplosion(new Rect(position, size));
                handler.postDelayed(() -> createExplosion(obstacle.getRect()), 150);
                handler.postDelayed(() -> createExplosion(other.getRect()), 300);
            } else if (other instanceof Player) {
                createExplosion(obstacle.getRect());
            }
        });
        obstacle.addOnDestroyListener(() -> {
            if (obstacle.getRect().getLeftPx() < 0) {
                score.increment();
                setScoreText();
            }
        });
        collisionLayer.add(obstacle);

        if (game.isRunning()) {
            handler.postDelayed(this::createObstacles, (int) (3000 / difficulty.get()));
        }
    }

    private void createScore() {
        Paint scorePaint = createPaint(SizeSystem.getInstance().heightToPx(2.75f));
        scoreObject = new TextObject(new Position(65, 5), scorePaint);
        overlayLayer.add(scoreObject);
        setScoreText();
    }

    @SuppressLint("StringFormatInvalid")
    private void setScoreText() {
        scoreObject.setText(getString(R.string.score, score.getScore()) + "\n" + getString(
            R.string.highscore,
            highScore
        ));
    }

    private void createMenuButton() {
        Position buttonPosition = new Position(90, 5);
        Size buttonSize = Size.squareFromWidth(5);
        Rect buttonRect = new Rect(buttonPosition, buttonSize);

        Button menuButton = new Button(buttonRect, assets.getPauseButtonBitmap());
        menuButton.addOnClickListener(() -> pauseGame(true));
        overlayLayer.add(menuButton);
    }

    private void createMenuDialog() {
        PauseMenuBinding dialogBinding = PauseMenuBinding.inflate(getLayoutInflater());
        dialogBinding.resume.setOnClickListener(v -> menu.dismiss());
        dialogBinding.exit.setOnClickListener(v -> finish());
        dialogBinding.restart.setOnClickListener(v -> {
            isRecreating = true;
            menu.dismiss();
            recreate();
        });

        menu = createDialogBuilder()
            .setMessage(getString(R.string.pause))
            .setView(dialogBinding.getRoot())
            .setOnDismissListener(v -> startGame())
            .create();
    }

    private void openMenu() {
        if (!menu.isShowing() && !isRecreating) {
            menu.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sounds.resumeMusic();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sounds.pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sounds.release();
    }

    @Override
    public void onBackPressed() {
        pauseGame(true);
    }

    private void startGame() {
        game.start();
        countdown();
    }

    private void countdown() {
        setLayerStatusIf(layer -> layer != countdownLayer, GameLayer.Status.DrawEnabled);

        Countdown countdown = new Countdown(new Position(50, 50), createPaint(120), sounds);
        countdown.addOnDestroyListener(() -> {
            setLayerStatusIf(layer -> layer != countdownLayer, GameLayer.Status.Enabled);
            if (!sounds.isMusicPlaying()) {
                sounds.playMusic(R.raw.game_music);
            }

            createObstacles();
            createPowerUps(true);
        });
        countdownLayer.add(countdown);
        countdown.start(3);
    }

    private void setLayerStatusIf(Predicate<GameLayer> predicate, GameLayer.Status status) {
        for (GameLayer layer : game.getLayers()) {
            if (predicate.test(layer)) {
                layer.setStatus(status);
            }
        }
    }

    private void pauseGame(boolean showMenu) {
        game.stop();
        game.saveState();
        score.save();
        if (showMenu) {
            openMenu();
        }
    }

    private void gameOver() {
        setLayerStatusIf(layer -> layer != animationLayer, GameLayer.Status.DrawEnabled);

        Rect playerRect = player.getRect();
        createExplosion(new Rect(
            playerRect.position,
            Size.squareFromWidth(playerRect.size.getX())
        )).addOnDestroyListener(() -> runOnUiThread(() -> {
            pauseGame(false);
            sounds.pauseMusic();
            gameOverMenu.show();
        }));
    }

    private void createGameOverDialog() {
        GameOverBinding gameOverBinding = GameOverBinding.inflate(getLayoutInflater());
        gameOverBinding.exit.setOnClickListener(v -> finish());
        gameOverBinding.restart.setOnClickListener(v -> {
            isRecreating = true;
            menu.dismiss();
            recreate();
        });

        gameOverMenu = createDialogBuilder()
            .setMessage(getString(R.string.game_over))
            .setView(gameOverBinding.getRoot())
            .setBackground(ContextCompat.getDrawable(this, R.drawable.game_over))
            .setCancelable(false)
            .create();
    }

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

    private Paint createPaint(float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        paint.setTypeface(assets.readFont());
        paint.setColor(Color.WHITE);

        return paint;
    }
}


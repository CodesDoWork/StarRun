package de.host.mobsys.starrun;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;

import androidx.appcompat.app.AlertDialog;

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
import de.host.mobsys.starrun.control.Sounds;
import de.host.mobsys.starrun.control.PreferenceInfo;
import de.host.mobsys.starrun.databinding.PauseMenuBinding;
import de.host.mobsys.starrun.models.Difficulty;
import de.host.mobsys.starrun.models.Score;
import de.host.mobsys.starrun.views.Animation;
import de.host.mobsys.starrun.views.Background;
import de.host.mobsys.starrun.views.Countdown;
import de.host.mobsys.starrun.views.Obstacle;
import de.host.mobsys.starrun.views.Player;

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
    private TextObject scoreObject;

    private boolean isRecreating = false;

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
        Player player = new Player(playerRect, playerSprite, difficulty);
        player.addOnMoveListener((x, y) -> backgroundLayer.translate(0, -y / 100));
        player.addOnCollisionListener(p -> gameOver());
        collisionLayer.add(player);

        Animation animation = new Animation(
            playerRect.position,
            assets.getExplosionAnimation(),
            12,
            300,
            Size.fromWidthAndHeight(15, 15)
        );
        animation.startAnimation();
        //Remove the comment signs below to start the animation
        //animationLayer.add(animation);
    }

    private void createObstacles() {
        Obstacle obstacle = Obstacle.createRandom(assets, difficulty);
        obstacle.addOnCollisionListener(p -> {
            Log.d("COLLISION", "collision at: " + p.x + ", " + p.y);
            sounds.playSound(R.raw.explosion);
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
        menuButton.addOnClickListener(this::pauseGame);
        overlayLayer.add(menuButton);
    }

    private void createMenuDialog() {
        PauseMenuBinding dialogBinding = PauseMenuBinding.inflate(getLayoutInflater());
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
        pauseGame();
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
        pauseGame();
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

    private void pauseGame() {
        game.stop();
        game.saveState();
        score.save();
        openMenu();
    }

    private void gameOver() {
        game.stop();
        score.save();
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

    private Paint createPaint(float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        paint.setTypeface(assets.readFont());
        paint.setColor(Color.WHITE);

        return paint;
    }
}


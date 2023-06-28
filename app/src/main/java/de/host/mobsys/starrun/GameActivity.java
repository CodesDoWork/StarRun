package de.host.mobsys.starrun;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;

import androidx.appcompat.app.AlertDialog;

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
import de.host.mobsys.starrun.databinding.PauseMenuBinding;
import de.host.mobsys.starrun.models.Difficulty;
import de.host.mobsys.starrun.models.Score;
import de.host.mobsys.starrun.views.Animation;
import de.host.mobsys.starrun.views.Background;
import de.host.mobsys.starrun.views.Obstacle;
import de.host.mobsys.starrun.views.Player;

public class GameActivity extends BaseActivity {

    private final GameLayer backgroundLayer = new GameLayer();
    private final GameLayer collisionLayer = new CollisionLayer();
    private final GameLayer animationLayer = new GameLayer();
    private final GameLayer overlayLayer = new GameLayer();

    private final Handler handler = new Handler();
    private final Difficulty difficulty = new Difficulty();

    private GameView game;
    private Assets assets;
    private Score score;
    private int highScore = 0;

    private AlertDialog menu = null;
    private TextObject scoreObject;

    private boolean isRecreating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRecreating = false;

        score = new Score(storage);
        score.addChangeListener(difficulty::setFromScore);
        assets = new Assets(getResources().getAssets());

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
        player.addOnCollisionListener(this::gameOver);
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
        Paint scorePaint = new Paint();
        scorePaint.setTextSize(SizeSystem.getInstance().heightToPx(2.75f));
        scorePaint.setAntiAlias(true);
        scorePaint.setTypeface(assets.readFont());
        scorePaint.setColor(Color.WHITE);

        scoreObject = new TextObject(new Position(62.5f, 5), scorePaint);
        setScoreText();
        overlayLayer.add(scoreObject);
    }

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

    private void gameOver() {
        game.stop();
        score.save();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
    }

    @Override
    public void onBackPressed() {
        pauseGame();
    }

    private void startGame() {
        game.start();
        createObstacles();
    }

    private void pauseGame() {
        game.stop();
        game.saveState();
        score.save();
        openMenu();
    }

    private void openMenu() {
        if (!menu.isShowing() && !isRecreating) {
            menu.show();
        }
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
}


package de.host.mobsys.starrun;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;

import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.views.Animation;
import de.host.mobsys.starrun.base.GameLayer;
import de.host.mobsys.starrun.base.GameView;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.base.size.systems.PercentSizeSystem;
import de.host.mobsys.starrun.base.views.CollisionLayer;
import de.host.mobsys.starrun.base.views.TextObject;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.control.PreferenceInfo;
import de.host.mobsys.starrun.views.Background;
import de.host.mobsys.starrun.views.Obstacle;
import de.host.mobsys.starrun.views.Player;

public class GameActivity extends BaseActivity {

    private final GameLayer backgroundLayer = new GameLayer();
    private final GameLayer collisionLayer = new CollisionLayer();
    private final GameLayer overlayLayer = new GameLayer();
    private final GameLayer animationLayer = new GameLayer();

    private final Handler handler = new Handler();

    private GameView game;
    private Assets assets;

    private int score = 0;
    private int highScore = 0;
    private TextObject scoreObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assets = new Assets(getResources().getAssets());
        highScore = storage.get(PreferenceInfo.HIGHSCORE);

        setupSizeSystem();
        setupGame();
    }

    private void setupSizeSystem() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        SizeSystem.setup(size.x, size.y);
        SizeSystem.setSizeSystem(new PercentSizeSystem());
    }

    private void setupGame() {
        game = new GameView(this);
        setContentView(game);
        game.start();

        game.add(backgroundLayer);
        game.add(collisionLayer);
        game.add(animationLayer);
        game.add(overlayLayer);

        createBackground();
        createPlayer();
        createObstacles();
        createScore();
    }

    private void createBackground() {
        Background background = new Background(110, assets);
        backgroundLayer.add(background);
    }

    private void createPlayer() {
        Bitmap playerSprite = assets.getPlayerBitmap();

        Rect playerRect = new Rect(
            new Position(5, 45),
            BitmapUtils.getSizeByWidth(playerSprite, 15)
        );
        Player player = new Player(playerRect, playerSprite);
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
        Obstacle obstacle = Obstacle.createRandom(assets);
        obstacle.addOnDestroyListener(() -> {
            if (obstacle.getRect().getLeftPx() < 0) {
                ++score;
                setScoreText();
            }
        });
        collisionLayer.add(obstacle);
        handler.postDelayed(this::createObstacles, 3000);
    }

    private void createScore() {
        Paint scorePaint = new Paint();
        scorePaint.setTextSize(28);
        scorePaint.setAntiAlias(true);
        scorePaint.setTypeface(assets.readFont());
        scorePaint.setColor(Color.WHITE);

        scoreObject = new TextObject(new Position(5, 5), scorePaint);
        setScoreText();
        overlayLayer.add(scoreObject);
    }

    private void setScoreText() {
        scoreObject.setText(getString(R.string.score, score) + "\n" + getString(
            R.string.highscore,
            highScore
        ));
    }

    private void gameOver() {
        game.stop();
        saveHighScore();
    }

    private void saveHighScore() {
        storage.set(PreferenceInfo.HIGHSCORE, Math.max(score, highScore));
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveHighScore();
    }
}


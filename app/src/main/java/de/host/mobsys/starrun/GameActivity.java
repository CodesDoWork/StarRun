package de.host.mobsys.starrun;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;

import de.host.mobsys.starrun.base.GameLayer;
import de.host.mobsys.starrun.base.GameView;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.base.size.systems.PercentSizeSystem;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.views.Asteroid;
import de.host.mobsys.starrun.views.Background;
import de.host.mobsys.starrun.views.CollisionLayer;
import de.host.mobsys.starrun.views.Player;
import de.host.mobsys.starrun.views.ScoreObject;

public class GameActivity extends BaseActivity {

    private final GameLayer backgroundLayer = new GameLayer();
    private final GameLayer collisionLayer = new CollisionLayer();
    private final GameLayer overlayLayer = new GameLayer();

    private final Handler handler = new Handler();

    private Assets assets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assets = new Assets(getResources().getAssets());

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
        GameView game = new GameView(this);
        setContentView(game);
        game.start();

        game.add(backgroundLayer);
        game.add(collisionLayer);
        game.add(overlayLayer);

        createBackground();
        createPlayer();
        createAsteroids();
        createScore();
    }

    private void createBackground() {
        Background background = new Background(
            Size.fromHeightAndAspectRatio(110, 2),
            assets
        );
        backgroundLayer.add(background);
    }

    private void createPlayer() {
        Rect playerRect = new Rect(
            new Position(5, 45),
            Size.fromWidthAndAspectRatio(15, 428 / 168f)
        );
        Player player = new Player(playerRect, assets.getPlayerBitmap());
        player.addOnMoveListener((x, y) -> backgroundLayer.translate(0, -y / 100));
        collisionLayer.add(player);
    }

    private void createAsteroids() {
        collisionLayer.add(Asteroid.createRandom(assets));
        handler.postDelayed(this::createAsteroids, 3000);
    }

    private void createScore() {
        Rect scoreRect = new Rect(
            new Position(80, 5),
            Size.fromWidthAndHeight(15, 15)
        );
        ScoreObject scoreObject = new ScoreObject(scoreRect, this);
        overlayLayer.add(scoreObject);
    }
}


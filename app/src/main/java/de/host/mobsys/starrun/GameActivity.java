package de.host.mobsys.starrun;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

import de.host.mobsys.starrun.base.GameLayer;
import de.host.mobsys.starrun.base.GameView;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.base.size.systems.PercentSizeSystem;
import de.host.mobsys.starrun.control.PreferenceStorage;
import de.host.mobsys.starrun.views.Background;
import de.host.mobsys.starrun.views.Player;

public class GameActivity extends AppCompatActivity {

    private final GameLayer backgroundLayer = new GameLayer();
    private final GameLayer collisionLayer = new GameLayer();
    private final GameLayer overlayLayer = new GameLayer();

    private PreferenceStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = new PreferenceStorage(this);

        setupSizeSystem();
        setupGame();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            hideSystemUi();
        }
    }

    private void hideSystemUi() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
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
    }

    private void createBackground() {
        Background background = new Background(
            Size.fromHeightAndAspectRatio(110, 2),
            loadAsset("space_bit_v2.png")
        );
        backgroundLayer.add(background);
    }

    private void createPlayer() {
        Rect playerRect = new Rect(
            new Position(5, 45),
            Size.fromWidthAndAspectRatio(15, 428 / 168f)
        );
        Player player = new Player(playerRect, loadAsset("ship_cut.png"));
        player.addOnMoveListener((x, y) -> backgroundLayer.translate(0, -y / 100));
        collisionLayer.add(player);
    }

    private Bitmap loadAsset(String fileName) {
        AssetManager assets = getResources().getAssets();
        try (InputStream playerSpriteStream = assets.open(fileName)) {
            return BitmapFactory.decodeStream(playerSpriteStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
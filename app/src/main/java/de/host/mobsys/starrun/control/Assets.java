package de.host.mobsys.starrun.control;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class to handle assets and their loading
 */
public class Assets {

    public static final String ASTEROIDS_DIR = "img/obstacles";
    public static final String BACKGROUND_DIR = "img/backgrounds";
    public static final String EXPLOSION_ANIMATION = "animation/explosion.png";
    public static final String FONT = "font/press_start_2p.ttf";
    public static final String PAUSE_BUTTON = "img/pause_button.png";
    public static final String PLAYER = "img/player.png";
    public static final String PLAYER_SHEET = "animation/player.png";
    public static final String POWER_UP_BOMD = "img/power-ups/bomb.png";
    public static final String POWER_UP_SHIELD = "img/power-ups/shield.png";
    public static final String POWER_UP_SHRINK = "img/power-ups/shrink.png";
    public static final String SHIELD = "img/shield.png";

    private final AssetManager assetManager;
    private final Random random = new Random();

    public Assets(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Bitmap getRandomObstacle() {
        return readBitmap(getRandomAsset(ASTEROIDS_DIR));
    }

    public Bitmap getRandomBackground() {
        return readBitmap(getRandomAsset(BACKGROUND_DIR));
    }

    public Bitmap getPauseButtonBitmap() {
        return readBitmap(PAUSE_BUTTON);
    }

    public Bitmap getPlayerBitmap() {
        return readBitmap(PLAYER);
    }

    public Bitmap getPlayerSpriteSheet() {
        return readBitmap(PLAYER_SHEET);
    }

    public Bitmap getShieldBitmap() {
        return readBitmap(SHIELD);
    }

    public Bitmap getExplosionAnimation() {
        return readBitmap(EXPLOSION_ANIMATION);
    }

    public Typeface readFont() {
        return Typeface.createFromAsset(assetManager, FONT);
    }

    public Bitmap readBitmap(String name) {
        AtomicReference<Bitmap> bitmapRef = new AtomicReference<>(null);
        readAsset(name, stream -> bitmapRef.set(BitmapFactory.decodeStream(stream)));

        return bitmapRef.get();
    }

    public void readAsset(String name, @NonNull ReadAssetCallback callback) {
        try (InputStream stream = assetManager.open(name)) {
            callback.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRandomAsset(String dir) {
        try {
            String[] assets = assetManager.list(dir);
            return dir + "/" + assets[random.nextInt(assets.length)];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    interface ReadAssetCallback {
        void read(InputStream spriteStream);
    }
}

package de.host.mobsys.starrun.control;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Assets {

    public static final String ASTEROIDS_DIR = "asteroids";
    public static final String BACKGROUND_DIR = "backgrounds";
    public static final String PLAYER = "player.png";

    private final AssetManager assetManager;
    private final Random random = new Random();

    public Assets(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Bitmap getRandomAsteroid() {
        return readBitmap(getRandomAsset(ASTEROIDS_DIR));
    }

    public Bitmap getRandomBackground() {
        return readBitmap(getRandomAsset(BACKGROUND_DIR));
    }

    public Bitmap getPlayerBitmap() {
        return readBitmap(PLAYER);
    }

    public void readAsset(String name, @NonNull ReadAssetCallback callback) {
        try (InputStream stream = assetManager.open(name)) {
            callback.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Bitmap readBitmap(String name) {
        AtomicReference<Bitmap> bitmapRef = new AtomicReference<>(null);
        readAsset(name, stream -> bitmapRef.set(BitmapFactory.decodeStream(stream)));

        return bitmapRef.get();
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

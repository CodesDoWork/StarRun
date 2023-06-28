package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.control.Assets;
import de.host.mobsys.starrun.models.Difficulty;

public class Background extends GameObject {

    private final Assets assets;
    private final float height;
    private final List<Bitmap> sprites = new ArrayList<>();

    public Background(float height, Assets assets, Difficulty difficulty) {
        super(new Position(0, (100 - height) / 2));
        velocity = new VelocityBuilder().left(1).build();

        this.height = height;
        this.assets = assets;

        int width = 0;
        while (width < SizeSystem.getDisplayWidth()) {
            addRandomSprite();
            width += sprites.get(sprites.size() - 1).getWidth();
        }
        addRandomSprite();

        difficulty.addChangeListener(value -> {
            velocity = new VelocityBuilder().left(difficulty.get()).build();
        });
    }

    @Override
    public void update(Duration elapsedTime) {
        super.update(elapsedTime);
        int rightEdge = position.getXPx() + sprites.get(0).getWidth();
        if (rightEdge <= 0) {
            position.setXPx(rightEdge);
            sprites.remove(0);
            addRandomSprite();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int spriteIdx = 0;
        for (
            int x = position.getXPx();
            x < SizeSystem.getDisplayWidth();
            x += sprites.get(spriteIdx - 1).getWidth()
        ) {
            canvas.drawBitmap(sprites.get(spriteIdx++), x, position.getYPx(), null);
        }
    }

    private void addRandomSprite() {
        Bitmap sprite = assets.getRandomBackground();
        sprites.add(BitmapUtils.scaleBitmap(sprite, BitmapUtils.getSizeByHeight(sprite, height)));
    }
}

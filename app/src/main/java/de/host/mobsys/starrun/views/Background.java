package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.base.GameObject;
import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.BitmapUtils;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.base.size.SizeSystem;
import de.host.mobsys.starrun.control.Assets;

public class Background extends GameObject {

    private static final Velocity BACKGROUND_VELOCITY = new VelocityBuilder().left(1).build();

    private final Assets assets;
    private final List<Bitmap> sprites = new ArrayList<>();

    public Background(Size size, Assets assets) {
        super(
            new Rect(new Position(0, -(size.getY() - 100) / 2), size),
            BACKGROUND_VELOCITY
        );

        this.assets = assets;
        int backgroundsNeeded = getBackgroundNeededCount();
        while (sprites.size() < backgroundsNeeded) {
            addRandomSprite();
        }
    }

    @Override
    public void update(Duration frameDuration) {
        super.update(frameDuration);
        if (rect.getLeftPx() + rect.size.getWidthPx() <= 0) {
            rect.position.setXPx(rect.getRightPx());
            sprites.remove(0);
            addRandomSprite();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int spriteIdx = 0;
        for (
            int x = rect.getLeftPx();
            x < SizeSystem.getDisplayWidth();
            x += rect.size.getWidthPx()
        ) {
            canvas.drawBitmap(sprites.get(spriteIdx++), x, rect.getTop(), null);
        }
    }

    private void addRandomSprite() {
        sprites.add(BitmapUtils.scaleBitmap(assets.getRandomBackground(), rect.size));
    }

    private int getBackgroundNeededCount() {
        int displayWidth = SizeSystem.getDisplayWidth();
        int backgroundWidth = rect.size.getWidthPx();

        return (int) (Math.ceil(1f * displayWidth / backgroundWidth) + 1);
    }
}

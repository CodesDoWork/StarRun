package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.time.Duration;

import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.base.size.SizeSystem;

public class Background extends BitmapObject {

    private static final Velocity BACKGROUND_VELOCITY = new VelocityBuilder().left(1).build();

    public Background(Size size, Bitmap sprite) {
        super(
            new Rect(new Position(0, -(size.getY() - 100) / 2), size),
            BACKGROUND_VELOCITY,
            sprite
        );
    }

    @Override
    public void update(Duration frameDuration) {
        super.update(frameDuration);
        if (rect.getLeftPx() + rect.size.getWidthPx() <= 0) {
            rect.position.setXPx(rect.getRightPx());
        }
    }

    @Override
    public void draw(Canvas canvas) {
        for (
            int x = rect.getLeftPx();
            x < SizeSystem.getDisplayWidth();
            x += rect.size.getWidthPx()
        ) {
            canvas.drawBitmap(sprite, x, 0, null);
        }
    }
}

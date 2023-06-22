package de.host.mobsys.starrun.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import de.host.mobsys.starrun.GameActivity;
import de.host.mobsys.starrun.base.GameLayer;
import de.host.mobsys.starrun.base.physics.Velocity;
import de.host.mobsys.starrun.base.physics.VelocityBuilder;
import de.host.mobsys.starrun.base.size.Position;
import de.host.mobsys.starrun.base.size.Rect;
import de.host.mobsys.starrun.base.size.Size;
import de.host.mobsys.starrun.base.size.SizeSystem;

public class CollisionObject extends BitmapObject {

    private static final Velocity Collision_VELOCITY = new VelocityBuilder().left(10).build();

    public CollisionObject(Rect rect, Bitmap sprite) {
        super(
            rect
            , Collision_VELOCITY,
                       sprite);

    }
    // Konstruktor und andere Methoden hier

    public Rect getCollisionRect() {
        // Gibt das Rechteck zurück, das die Kollisionsbereiche des Objekts repräsentiert
        // Dieses Rechteck sollte den Bereich um das Bild darstellen, mit dem der Spieler kollidieren kann
        return rect;
    }


}
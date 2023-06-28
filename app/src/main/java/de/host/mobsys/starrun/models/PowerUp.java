package de.host.mobsys.starrun.models;

import de.host.mobsys.starrun.control.Assets;

public enum PowerUp {
    Bomb(Assets.POWER_UP_BOMD),
    Shield(Assets.POWER_UP_SHIELD),
    Shrink(Assets.POWER_UP_SHRINK);

    public final String asset;

    PowerUp(String asset) {
        this.asset = asset;
    }
}

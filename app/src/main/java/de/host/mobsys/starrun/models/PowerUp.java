package de.host.mobsys.starrun.models;

import androidx.annotation.RawRes;

import de.host.mobsys.starrun.R;
import de.host.mobsys.starrun.control.Assets;

/**
 * Enum to represent each power-up with sprite asset and audio id.
 */
public enum PowerUp {
    Bomb(Assets.POWER_UP_BOMD, R.raw.explosion),
    Shield(Assets.POWER_UP_SHIELD, R.raw.shield),
    Shrink(Assets.POWER_UP_SHRINK, R.raw.shrink);

    public final String asset;
    public final @RawRes int audioId;

    PowerUp(String asset, @RawRes int audioId) {
        this.asset = asset;
        this.audioId = audioId;
    }
}

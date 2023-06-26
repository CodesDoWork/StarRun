package de.host.mobsys.starrun.control;

public class RandomUtils {

    public static float between(float min, float max) {
        return (float) ((Math.random() * (max - min + 1)) + min);
    }
}

package de.host.mobsys.starrun.control;

import androidx.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * A class containing all necessary information about a SharedPreferences-entry.
 **/
public class PreferenceInfo<T> {

    public final String key;
    public final Type type;
    public final T defaultValue;

    /**
     * @param key          A unique key representing this entry
     * @param type         Type of the preference value
     * @param defaultValue A default value if entry is not found in SharedPreferences
     */
    public PreferenceInfo(String key, Type type, @Nullable T defaultValue) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public PreferenceInfo(String key, Type type) {
        this(key, type, null);
    }
}

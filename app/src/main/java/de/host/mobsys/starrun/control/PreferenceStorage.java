package de.host.mobsys.starrun.control;

import static de.host.mobsys.starrun.control.Json.GSON;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * A class to read/write any objects from/to SharedPreferences.
 * SharedPreferences are saved in xml-format at `/data/data/{{package}}/shared_preferences`.
 */
public class PreferenceStorage {

    private final SharedPreferences preferences;

    public PreferenceStorage(Context context) {
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    /**
     * Reads and deserializes the value represented by the info param.
     *
     * @return the value saved in SharedPreferences or, if entry not found, the default value of info
     */
    public <T> T get(PreferenceInfo<T> info) {
        T value = GSON.fromJson(preferences.getString(info.key, null), info.type);
        return value == null ? info.defaultValue : value;
    }

    /**
     * Serialized and saves the specified value into SharedPreferences.
     *
     * @param info  PreferenceInfo holding the key under which the value is stored.
     * @param value The value to save.
     */
    public <T> void set(PreferenceInfo<T> info, T value) {
        preferences.edit().putString(info.key, GSON.toJson(value)).apply();
    }

    /**
     * Removes the entry represented by info from SharedPreferences.
     *
     * @param info PreferenceInfo for the entry to remove.
     */
    public void delete(PreferenceInfo<?> info) {
        preferences.edit().remove(info.key).apply();
    }
}

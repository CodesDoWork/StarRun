package de.host.mobsys.starrun.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Contains json oriented functions.
 */
public abstract class Json {
    public static final Gson GSON = new GsonBuilder().create();
}

package de.host.mobsys.starrun.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Json {
    public static final Gson GSON = new GsonBuilder().create();
}

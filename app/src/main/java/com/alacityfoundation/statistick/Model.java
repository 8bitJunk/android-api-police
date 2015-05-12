package com.alacityfoundation.statistick;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by ryan on 08/05/2015.
 */
public class Model {

    // turn the model to a string using JSON
    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        return gson.toJson(this);
    }
}

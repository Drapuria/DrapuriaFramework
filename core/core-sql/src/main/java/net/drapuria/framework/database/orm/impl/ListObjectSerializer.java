/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.orm.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.Component;

import java.lang.reflect.Type;
import java.util.List;


//@Component
public class ListObjectSerializer implements ObjectSerializer<List, String> {

    private static final Gson GSON;

    private static final Type TYPE_TOKEN = new TypeToken<List<?>>() {
    }.getType();

    static {
        GSON = new GsonBuilder().registerTypeAdapter(Class.class, (JsonDeserializer<Class>) (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject object = jsonElement.getAsJsonObject();
            try {
                return Class.forName(object.get("type").getAsString());
            } catch (ClassNotFoundException e) {
                return null;
            }
        })/*.registerTypeAdapter(Class.class, (JsonSerializer<Class>) (aClass, type, jsonSerializationContext) -> {
            JsonObject object = new JsonObject();
            object.addProperty("type", aClass.getName());
            return object;
        })*/
                .create();
    }

    @Override
    public String serialize(List input) {
        return GSON.toJson(input);
    }

    @Override
    public List deserialize(String output) {
        return GSON.fromJson(output, TYPE_TOKEN);
    }

    @Override
    public Class<List> inputClass() {
        return List.class;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}

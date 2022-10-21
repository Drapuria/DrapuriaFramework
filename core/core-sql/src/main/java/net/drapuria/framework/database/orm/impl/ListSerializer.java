package net.drapuria.framework.database.orm.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.util.TypeResolver;

import java.lang.reflect.Type;
import java.util.List;

public class ListSerializer implements ObjectSerializer<List<?>, String> {

    private static final Class<List<?>> uuidListClazz;

    private static final Gson GSON;

    private static final Type TYPE_TOKEN = new TypeToken<List<?>>() {
    }.getType();

    static {
        Class<?>[] array = findIdType();
        uuidListClazz = (Class<List<?>>) array[0];
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
    public String serialize(List<?> input) {
        return null;
    }

    @Override
    public List<?> deserialize(String output) {
        return null;
    }

    @Override
    public Class<List<?>> inputClass() {
        return uuidListClazz;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }

    private static Class<?>[] findIdType() {
        TypeResolver.enableCache();
        return TypeResolver.resolveRawArguments(ObjectSerializer.class, ListSerializer.class);
    }

}

package net.drapuria.framework.database.orm.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.services.Component;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Component
public class StringMapObjectSerializer implements ObjectSerializer<Map, String> {

    private static Gson gson = new Gson();

    private static final Type TYPE_TOKEN = new TypeToken<HashMap<String, String>>() {
    }.getType();

    @Override
    public String serialize(Map input) {
        return gson.toJson(input);
    }

    @Override
    public HashMap<String, String> deserialize(String output) {
        return gson.fromJson(output, TYPE_TOKEN);
    }

    @Override
    public Class<Map> inputClass() {
        return Map.class;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}
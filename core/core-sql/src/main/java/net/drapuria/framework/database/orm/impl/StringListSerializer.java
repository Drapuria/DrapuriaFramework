package net.drapuria.framework.database.orm.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.Component;

import java.lang.reflect.Type;
import java.util.List;

//@Component
public class StringListSerializer implements ObjectSerializer<List<String>, String> {

    private static Gson gson = new Gson();

    private static final Type TYPE_TOKEN = new TypeToken<List<String>>() {
    }.getType();

    private static final Class<List<String>> inputClass = (Class<List<String>>) new TypeToken<List<String>>() {
    }.getRawType();



    @Override
    public String serialize(List<String> strings) {
        return gson.toJson(strings);
    }

    @Override
    public List<String> deserialize(String o) {
        return gson.fromJson(o, TYPE_TOKEN);
    }

    @Override
    public Class<List<String>> inputClass() {
        return inputClass;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}

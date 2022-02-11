/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.orm.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.Component;

import java.lang.reflect.Type;
import java.util.List;


@Component
public class ListObjectSerializer implements ObjectSerializer<List, String> {

    private static final Gson GSON = new Gson();

    private static final Type TYPE_TOKEN = new TypeToken<List<?>>() {
    }.getType();

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

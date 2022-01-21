/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.orm.impl;

import net.drapuria.framework.ObjectSerializer;

import java.util.UUID;

public class BinaryUUIDObjectSerializer implements ObjectSerializer<UUID, String> {
    @Override
    public String serialize(UUID input) {
        return input.toString().replaceAll("-", "");
    }

    @Override
    public UUID deserialize(String output) {
        return UUID.fromString(output.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    @Override
    public Class<UUID> inputClass() {
        return UUID.class;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}

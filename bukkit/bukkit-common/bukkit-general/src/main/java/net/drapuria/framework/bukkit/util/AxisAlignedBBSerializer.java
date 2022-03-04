/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.util;

import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.Component;

import java.util.Arrays;

@Component
public class AxisAlignedBBSerializer implements ObjectSerializer<AxisAlignedBB, String> {
    @Override
    public String serialize(AxisAlignedBB input) {
        return input.minX + ":" + input.minY + ":" + input.minZ + ":" + input.maxX + ":" + input.maxY + ":" + input.maxZ;
    }

    @Override
    public AxisAlignedBB deserialize(String output) {
       try {
           Double[] arr = Arrays.stream(output.split(":")).map(Double::parseDouble).toArray(Double[]::new);
           return AxisAlignedBB.getBoundingBox(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
       } catch (Exception e) {
           return null;
       }
    }

    @Override
    public Class<AxisAlignedBB> inputClass() {
        return AxisAlignedBB.class;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}

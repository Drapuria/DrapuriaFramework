/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.orm.utils;

import net.drapuria.framework.util.Utility;

import java.util.Arrays;
import java.util.Collection;

public class SQLUtil {

    public static String join(String[] toJoin) {
        return Utility.joinToString(toJoin, ",");
    }

    public static String join(Collection<String> toJoin) {
        return Utility.joinToString(toJoin, ",");
    }

    public static String getQuestionMarks(int count) {
        final String[] array = new String[count];
        Arrays.fill(array, "?");
        return join(array);
    }

    public static boolean isPrimitiveOrString(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class
                || clazz == Float.class || clazz == Double.class || clazz == Boolean.class
                || clazz == Character.class || clazz == String.class;
    }

    public static Class<?> wrapPrimitive(Class<?> type) {
        return Utility.wrapPrimitive(type);
    }
}

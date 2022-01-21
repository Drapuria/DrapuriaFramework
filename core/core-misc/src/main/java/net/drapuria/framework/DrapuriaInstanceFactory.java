/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework;

public class DrapuriaInstanceFactory {

    public static Object createInstanceOf(Class<?> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException ignored) { }
        return null;
    }

}

/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2021 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.drapuria.framework.bukkit.reflection.util;

import java.lang.annotation.Annotation;

public class SubclassUtil {
    public static Class<? extends Enum<?>> getEnumSubClass(Class<?> cls, String name) {
        return (Class<? extends Enum<?>>) getSubClass(cls, name);
    }

    public static Class<? extends Enum<?>> getEnumSubClass(Class<?> cls, int index) {
        return (Class<? extends Enum<?>>) getSubClass(cls, index);
    }

    public static Class<?> getSubClass(Class<?> cls, String name) {
        if (cls == null) {
            return null;
        }
        for (Class<?> subClass : cls.getDeclaredClasses()) {
            if (subClass.getSimpleName().equals(name)) {
                return subClass;
            }
        }
        return null;
    }

    public static Class<?> getSubClass(Class<?> cls, int index) {
        if (cls == null) {
            return null;
        }
        int currentIndex = 0;
        for (Class<?> subClass : cls.getDeclaredClasses()) {
            if (index == currentIndex++) {
                return subClass;
            }
        }
        return null;
    }

    public static Class<?> getSubClass(Class<?> cls, Annotation annotation, int index) {
        int currentIndex = 0;
        for (Class<?> subClass : cls.getDeclaredClasses()) {
            if (subClass.isAnnotationPresent(annotation.getClass())) {
                if (index == currentIndex++) {
                    return subClass;
                }
            }
        }
        return null;
    }
}
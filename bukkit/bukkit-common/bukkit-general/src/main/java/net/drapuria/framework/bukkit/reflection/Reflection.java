package net.drapuria.framework.bukkit.reflection;


import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Reflection {
    //FIELDS
    public static Field[] getFields(Class<?> cls) {
        Field[] declaredFields = cls.getDeclaredFields();
        for (Field f : declaredFields) {
            f.setAccessible(true);
        }
        return declaredFields;
    }

    public static Field getField(final Class<?> cls, final String name) {
        for (final Field f : getFields(cls)) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        if (cls.getSuperclass() != null) {
            return getField(cls.getSuperclass(), name);
        }
        return null;
    }

    public static Field getField(final Class<?> cls, final Class<?> dataType, final int index) {
        if (dataType == null || cls == null) {
            return null;
        }
        int currentIndex = 0;
        for (final Field f : getFields(cls)) {
            if (dataType.isAssignableFrom(f.getType())) {
                if (currentIndex++ == index) {
                    return f;
                }
            }
        }
        if (cls.getSuperclass() != null) {
            return getField(cls.getSuperclass(), dataType, index);
        }
        return null;
    }

    public static Field getField(final Class<?> cls, final Class<?> dataType, final int index, boolean ignoreStatic) {
        if (dataType == null || cls == null) {
            return null;
        }
        int currentIndex = 0;
        for (final Field f : getFields(cls)) {
            if (dataType.isAssignableFrom(f.getType()) && (!ignoreStatic || !Modifier.isStatic(f.getModifiers()))) {
                if (currentIndex++ == index) {
                    return f;
                }
            }
        }
        if (cls.getSuperclass() != null) {
            return getField(cls.getSuperclass(), dataType, index);
        }
        return null;
    }

    public static Field getField(final Class<?> cls, final int index) {
        try {
            return getFields(cls)[index];
        } catch (Exception ex) {
            if (cls.getSuperclass() != null) {
                return getFields(cls.getSuperclass())[index];
            }
        }
        return null;
    }

    //METHODS
    public static List<Method> getMethods(Class<?> cls, String name, Class<?>... params) {
        List<Method> methods = new ArrayList<>();
        for (Method m : cls.getDeclaredMethods()) {
            if ((params == null || Arrays.equals(m.getParameterTypes(), params)) && name.equals(m.getName())) {
                m.setAccessible(true);
                methods.add(m);
            }
        }
        return methods;
    }

    public static Method getMethod(final Class<?> cls, final int index, final Class<?>... params) {
        int currentIndex = 0;
        for (final Method m : cls.getDeclaredMethods()) {
            if ((params == null || Arrays.equals(m.getParameterTypes(), params)) && index == currentIndex++) {
                m.setAccessible(true);
                return m;
            }
        }
        if (cls.getSuperclass() != null) {
            return getMethod(cls.getSuperclass(), index, params);
        }
        return null;
    }

    public static Method getMethod(Class<?> cls, Class<?> returning, int index, Class<?>... params) {
        int currentIndex = 0;
        for (Method m : cls.getDeclaredMethods()) {
            if (Arrays.equals(m.getParameterTypes(), params)
                    && (returning == null || m.getReturnType().equals(returning))
                    && index == currentIndex++) {
                m.setAccessible(true);
                return m;
            }
        }
        if (cls.getSuperclass() != null) {
            return getMethod(cls.getSuperclass(), null, index, params);
        }
        return null;
    }

    public static Method getMethod(final Class<?> cls, final String name, Class<?> returning, Class<?>... params) {
        for (final Method m : cls.getDeclaredMethods()) {
            if (m.getName().equals(name)
                    && Arrays.equals(m.getParameterTypes(), params) &&
                    (returning == null || m.getReturnType().equals(returning))) {
                m.setAccessible(true);
                return m;
            }
        }
        if (cls.getSuperclass() != null) {
            return getMethod(cls.getSuperclass(), name, null, params);
        }
        return null;
    }

    public static Method getMethod(final Class<?> cls, final String name, final int index) {
        if (cls == null) {
            return null;
        }
        int currentIndex = 0;
        for (final Method m : cls.getDeclaredMethods()) {
            if (m.getName().equals(name) && index == currentIndex++) {
                m.setAccessible(true);
                return m;
            }
        }
        if (cls.getSuperclass() != null) {
            return getMethod(cls.getSuperclass(), name, index);
        }
        return null;
    }

    public static Method getMethod(final Class<?> cls, final Class<?> returning, final int index) {
        if (cls == null) {
            return null;
        }
        int currentIndex = 0;
        for (final Method m : cls.getDeclaredMethods()) {
            if ((returning == null || m.getReturnType().equals(returning)) && index == currentIndex++) {
                m.setAccessible(true);
                return m;
            }
        }
        if (cls.getSuperclass() != null) {
            return getMethod(cls.getSuperclass(), returning, index);
        }
        return null;
    }

    public static Method getMethodCheckContainsString(Class<?> cls, String nameContainsThisStr, Class<?> returning) {
        if (cls == null) {
            return null;
        }
        for (Method m : cls.getDeclaredMethods()) {
            if (m.getName().contains(nameContainsThisStr) && (returning == null || m.getReturnType().equals(returning))) {
                m.setAccessible(true);
                return m;
            }
        }
        if (cls.getSuperclass() != null) {
            return getMethodCheckContainsString(cls.getSuperclass(), nameContainsThisStr, returning);
        }
        return null;
    }

    public static Method getMethod(final Class<?> cls, final String name, final Class<?> returning) {
        if (cls == null) {
            return null;
        }
        for (final Method m : cls.getDeclaredMethods()) {
            if (m.getName().equals(name)
                    && (returning == null || m.getReturnType().equals(returning))) {
                m.setAccessible(true);
                return m;
            }
        }
        if (cls.getSuperclass() != null) {
            return getMethod(cls.getSuperclass(), name, returning);
        }
        return null;
    }

    @Nullable
    public static Class<?> getClassByNameWithoutException(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
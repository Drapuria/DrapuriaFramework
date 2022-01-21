/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util;

import com.google.common.collect.ImmutableMap;
import net.drapuria.framework.FrameworkMisc;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked", "rawtypes", "unused", "ConstantConditions"})
public class Utility {

    public static <T> Constructor<T> getConstructor(Class<T> parentClass, Class<?>... parameterTypes) {
        try {
            return parentClass.getConstructor(parameterTypes);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static <T> String joinToString(final T[] array) {
        return array == null ? "null" : joinToString(Arrays.asList(array));
    }

    public static <T> String joinToString(final T[] array, final String delimiter) {
        return array == null ? "null" : joinToString(Arrays.asList(array), delimiter);
    }

    public static <T> String joinToString(final Iterable<T> array) {
        return array == null ? "null" : joinToString(array, ", ");
    }

    public static <T> String joinToString(final Iterable<T> array, final String delimiter) {
        return join(array, delimiter, object -> object == null ? "" : object.toString());
    }

    public static <T> String join(final Iterable<T> array, final String delimiter, final Stringer<T> stringer) {
        final Iterator<T> it = array.iterator();
        StringBuilder message = new StringBuilder();

        while (it.hasNext()) {
            final T next = it.next();

            if (next != null)
                message.append(stringer.toString(next)).append(it.hasNext() ? delimiter : "");
        }

        return message.toString();
    }

    public static <T> List<Field> getAllFields(Class clazz) {
        List<Class> classes = new ArrayList<>();
        while (clazz != Object.class) {
            classes.add(clazz);
            clazz = clazz.getSuperclass();
        }

        Collections.reverse(classes);
        return classes.stream()
                .map(Class::getDeclaredFields)
                .flatMap(Stream::of)
                .collect(Collectors.toList());
    }

    public static Collection<Class<?>> getSuperAndInterfaces(Class<?> type) {
        Set<Class<?>> result = new HashSet<>();
        Set<Class<?>> superclasses = new HashSet<>();

        {
            Class<?> superclass = type;
            while (superclass != null && superclass != Object.class) {
                result.add(superclass);
                superclasses.add(superclass);

                superclass = superclass.getSuperclass();
            }
        }

        while (superclasses.size() > 0) {
            List<Class<?>> clone = new ArrayList<>(superclasses);
            superclasses.clear();

            for (Class<?> superclass : clone) {
                result.add(superclass);

                Class<?>[] interfaces = superclass.getInterfaces();
                if (interfaces != null && interfaces.length > 0) {
                    superclasses.addAll(Arrays.asList(interfaces));
                }
            }
        }

        return result;
    }

    public interface Stringer<T> {

        /**
         * Convert the given object into a string
         *
         * @param object
         * @return String
         */
        String toString(T object);
    }

    public static void tryCatch(ExceptionRunnable runnable) {
        Utility.tryCatch(runnable, throwable -> {
            throw new RuntimeException(throwable);
        });
    }

    public static void tryCatch(ExceptionRunnable runnable, Consumer<Throwable> handleThrow) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            handleThrow.accept(throwable);
        }
    }

    public static <T> Class<T> wrapPrimitive(Class<T> c) {
        return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
    }

    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS
            = new ImmutableMap.Builder<Class<?>, Class<?>>()
            .put(boolean.class, Boolean.class)
            .put(byte.class, Byte.class)
            .put(char.class, Character.class)
            .put(double.class, Double.class)
            .put(float.class, Float.class)
            .put(int.class, Integer.class)
            .put(long.class, Long.class)
            .put(short.class, Short.class)
            .put(void.class, Void.class)
            .build();

    /**
     * Returns the name of the class, as the JVM would output it. For instance, for an int, "I" is returned, for an
     * array of Objects, "[Ljava/lang/Object;" is returned. If the input is null, null is returned.
     *
     * @param clazz Java Class
     * @return JVM Name as {@link String}
     */
    public static String getJVMName(Class clazz) {
        if(clazz == null) {
            return null;
        }
        //For arrays, .getName() is fine.
        if(clazz.isArray()) {
            return clazz.getName().replace('.', '/');
        }
        if(clazz == boolean.class) {
            return "Z";
        } else if(clazz == byte.class) {
            return "B";
        } else if(clazz == short.class) {
            return "S";
        } else if(clazz == int.class) {
            return "I";
        } else if(clazz == long.class) {
            return "J";
        } else if(clazz == float.class) {
            return "F";
        } else if(clazz == double.class) {
            return "D";
        } else if(clazz == char.class) {
            return "C";
        } else {
            return "L" + clazz.getName().replace('.', '/') + ";";
        }
    }

    /**
     * Generically and dynamically returns the array class type for the given class type. The dynamic equivalent of
     * sending {@code String.class} and getting {@code String[].class}. Works with array types as well.
     * @param clazz The class to convert to an array type.
     * @return The array type of the input class.
     */
    public static Class<?> getArrayClassFromType(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        try {
            return Class.forName("[" + getJVMName(clazz).replace('/', '.'));
        } catch(ClassNotFoundException ex) {
            // This cannot naturally happen, as we are simply creating an array type for a real type that has
            // clearly already been loaded.
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }

    public static Type[] getGenericTypes(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }
        return null;
    }

    public static boolean isParametersEquals(Type[] parametersA, Type[] parametersB) {
        boolean equal = true;
        if (parametersA.length != parametersB.length) {
            return false;
        }

        for (int i = 0; i < parametersA.length; i++) {
            Type typeA = parametersA[i];
            if (typeA instanceof Class) {
                typeA = Utility.wrapPrimitive((Class<?>) typeA);
            }

            Type typeB = parametersB[i];
            if (typeB instanceof Class) {
                typeB = Utility.wrapPrimitive((Class<?>) typeB);
            }
            if (typeA != typeB) {
                equal = false;
                break;
            }
        }
        return equal;
    }

    public static <I, R> R[] toArrayType(I[] originalArray, Class<R> resultType, Function<I, R> transfer) {
        R[] result = (R[]) Array.newInstance(resultType, originalArray.length);
        for (int i = 0; i < result.length; i++) {
            result[i] = transfer.apply(originalArray[i]);
        }
        return result;
    }

    public static void resolveLinkageError() {
        try {
            Class.forName("net.drapuria.framework.util.AccessUtil");
           // Class.forName("org.springframework.expression.ExpressionParser");
        } catch (Throwable throwable) {
            FrameworkMisc.PLATFORM.getLogger().warn("Something wrong while resolving LinkageError...");
        }
    }

}

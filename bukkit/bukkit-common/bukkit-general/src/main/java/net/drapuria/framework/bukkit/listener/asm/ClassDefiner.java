/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.listener.asm;

import lombok.NonNull;

public interface ClassDefiner {

    /**
     * Returns if the defined classes can bypass access checks
     *
     * @return if classes bypass access checks
     */
    public default boolean isBypassAccessChecks() {
        return false;
    }

    /**
     * Define a class
     *
     * @param parentLoader the parent classloader
     * @param name         the name of the class
     * @param data         the class data to load
     * @return the defined class
     * @throws ClassFormatError     if the class data is invalid
     * @throws NullPointerException if any of the arguments are null
     */
    @NonNull
    public Class<?> defineClass(@NonNull ClassLoader parentLoader, @NonNull String name, @NonNull byte[] data);

    @NonNull
    public static ClassDefiner getInstance() {
        return SafeClassDefiner.INSTANCE;
    }

}

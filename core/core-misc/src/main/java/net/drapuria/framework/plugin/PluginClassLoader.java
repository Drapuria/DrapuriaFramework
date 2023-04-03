/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.plugin;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class PluginClassLoader {

    private static final Logger LOGGER = LogManager.getLogger(PluginClassLoader.class);
    private final URLClassLoader classLoader;
    private ClassPathAppender classPathAppender;

    @SuppressWarnings("Guava") // we can't use java.util.Function because old Guava versions are used at runtime
    private final Supplier<Method> addUrlMethod;

    public PluginClassLoader(ClassLoader classLoader) throws IllegalStateException {

        if (classLoader instanceof URLClassLoader) {
            this.classLoader = (URLClassLoader) classLoader;
        } else {
            throw new IllegalStateException("ClassLoader is not instance of URLClassLoader");
        }
        classPathAppender = null;

        this.addUrlMethod = Suppliers.memoize(() -> {
            if (isJava9OrNewer()) {
                LOGGER.info("It is safe to ignore any warning printed following this message " +
                        "starting with 'WARNING: An illegal reflective access operation has occurred, Illegal reflective " +
                        "access by " + getClass().getName() + "'. This is intended, and will not have any impact on the " +
                        "operation of Drapuria.");
            }

            try {
                Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrlMethod.setAccessible(true);
                return addUrlMethod;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public PluginClassLoader(ClassLoader classLoader, ClassPathAppender appender) {
        this(classLoader);
        this.classPathAppender = appender;
    }


    public void addJarToClasspath(Path file) {
        if (this.classPathAppender != null) {
            this.classPathAppender.addJarToClassPath(file);
            return;
        }
        try {
            Method addUrlMethod = this.addUrlMethod.get();
            addUrlMethod.setAccessible(true);
            addUrlMethod.invoke(this.classLoader, file.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    public static boolean isJava9OrNewer() {
        try {
            // method was added in the Java 9 release
            Runtime.class.getMethod("version");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
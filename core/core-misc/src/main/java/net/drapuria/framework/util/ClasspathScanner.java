/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util;

import com.google.common.collect.ImmutableSet;
import io.github.fastclasspathscanner.ClassInfo;
import io.github.fastclasspathscanner.FastClasspathScanner;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class ClasspathScanner {

    private static final  List<String> ignoredPaths = new ArrayList<>();

    public static void addIgnoredPath(String path) {
        ignoredPaths.add(path);
    }

    private final CodeSource codeSource;
    private final String packageName;

    public ClasspathScanner() {
        this.codeSource = this.getClass().getProtectionDomain().getCodeSource();
        this.packageName = "";
    }

    public ClasspathScanner(CodeSource codeSource, String packageName) {
        this.codeSource = codeSource;
        this.packageName = packageName;
    }


    protected void scan() {
        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile;
        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw (new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e));
        }
        queryResult(new FastClasspathScanner()
                .enableAllInfo()
                .blacklistPackages(ignoredPaths.toArray(new String[0]))
             //   .whitelistPaths(this.packageName)
                .scan().getAllClasses());
        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
                /*
        Enumeration<JarEntry> entries = jarFile.entries();
        Collection<Class<?>> classes = new ArrayList<>();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;

            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length()))
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");

            if (className != null) {
                Class<?> clazz = null;
                System.out.println(className);
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    continue;
                }
                classes.add(clazz);
            }
        }
        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        queryResult(ImmutableSet.copyOf(classes));
               */
    }

    public abstract void queryResult(Collection<ClassInfo> classes);


    public static CodeSource getCodeSourceOf(Object object) {
        return object.getClass().getProtectionDomain().getCodeSource();
    }

}

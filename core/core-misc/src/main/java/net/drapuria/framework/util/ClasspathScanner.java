/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.fastclasspathscanner.ClassInfo;
import io.github.fastclasspathscanner.ClassInfoList;
import io.github.fastclasspathscanner.FastClasspathScanner;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ClasspathScanner {

    private static final LoadingCache<String, ClassInfoList> classCache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, ClassInfoList>() {
                @Override
                public @Nullable ClassInfoList load(@NonNull String s) throws Exception {
                    return new FastClasspathScanner()
                            .enableAllInfo()
                           // .whitelistPackages(s.split(">>>>")[1])
                            .blacklistPackages(ignoredPaths.toArray(new String[0]))
                            //   .whitelistPaths(this.packageName)
                            .whitelistJars(s.split(">>>>")[2])
                            .scan(1000).getAllClasses();
                }
            });

    private static final List<String> ignoredPaths = new ArrayList<>();
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
        int length = jarPath.split("/").length;
        queryResult(classCache.get(resource.toExternalForm() + ">>>>" + "PACKAGE" + ">>>>" + jarPath.split("/")[length - 1]));
    }

    public abstract void queryResult(Collection<ClassInfo> classes);


    public static CodeSource getCodeSourceOf(Object object) {
        return object.getClass().getProtectionDomain().getCodeSource();
    }

}

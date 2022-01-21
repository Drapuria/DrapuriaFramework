/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util;


import io.github.fastclasspathscanner.ClassInfo;
import io.github.fastclasspathscanner.MethodInfo;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.CodeSource;
import java.util.*;


public class FastMethodAnnotationScanner extends ClasspathScanner {

    @Getter
    private Map<ClassInfo, MethodInfo[]> results = new HashMap<>();
    private final Class<? extends Annotation> annotation;

    public FastMethodAnnotationScanner(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
        scan();
    }

    public FastMethodAnnotationScanner(CodeSource codeSource, Class<? extends Annotation> annotation) {
        super(codeSource, "");
        this.annotation = annotation;
        scan();
    }

    @Override
    public void queryResult(Collection<ClassInfo> classes) {
        results = classes.stream()
                .filter(classInfo -> classInfo.getMethodInfo().stream()
                        .anyMatch(methodInfo -> methodInfo.getAnnotationInfo().stream()
                                .anyMatch(annotationInfo -> annotationInfo.getName().equals(annotation.getName()))))
                .map(classInfo -> {
                    Map<ClassInfo, MethodInfo[]> map = new HashMap<>();
                    map.put(classInfo, classInfo.getMethodInfo().stream()
                            .filter(methodInfo -> methodInfo.getAnnotationInfo().stream().anyMatch(annotationInfo -> annotationInfo.getName().equals(annotationInfo.getName())))
                            .toArray(MethodInfo[]::new));
                    return map;
                })
                .reduce(new HashMap<>(), MethodAnnotationScanner::reduceInto);
    }

    static <K, V> Map<K, V> reduceInto(Map<K, V> reduceInto, Map<K, V> valuesToAdd) {
        reduceInto.putAll(valuesToAdd);
        return reduceInto;
    }
}

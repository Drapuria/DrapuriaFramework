/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util;

import io.github.fastclasspathscanner.ClassInfo;

import java.lang.annotation.Annotation;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TypeAnnotationScanner extends ClasspathScanner {

    private final Class<? extends Annotation> annotation;

    private final List<Class<?>> result = new ArrayList<>();

    public TypeAnnotationScanner(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
        scan();
    }

    public TypeAnnotationScanner(CodeSource codeSource, Class<? extends Annotation> annotation) {
        super(codeSource, "");
        this.annotation = annotation;
        scan();
    }

    public TypeAnnotationScanner(CodeSource codeSource, String packageName, Class<? extends Annotation> annotation) {
        super(codeSource, packageName);
        this.annotation = annotation;
        scan();
    }


    public List<Class<?>> getResult() {
        return this.result;
    }

    @Override
    public void queryResult(Collection<ClassInfo> classes) {
        this.result.addAll(classes.stream()
                .filter(classInfo -> classInfo.hasAnnotation(annotation.getName()))
                .map(ClassInfo::loadClass)
                .collect(Collectors.toList()));
    }
}

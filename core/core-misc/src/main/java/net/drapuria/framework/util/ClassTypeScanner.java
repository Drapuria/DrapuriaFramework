/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util;

import io.github.fastclasspathscanner.ClassInfo;

import java.security.CodeSource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ClassTypeScanner extends ClasspathScanner {

    private final Class<?> type;
    private List<Class<?>> result;

    public ClassTypeScanner(Class<?> type) {
        this.type = type;
        super.scan();
    }

    public ClassTypeScanner(CodeSource codeSource, String packageName, Class<?> type) {
        super(codeSource, packageName);
        this.type = type;
        super.scan();
    }


    @Override
    public void queryResult(Collection<ClassInfo> classes) {
        this.result = classes.stream().filter(classInfo -> classInfo.extendsSuperclass(type.getName()))
                .map(ClassInfo::loadClass).collect(Collectors.toList());
    }

    public List<Class<?>> getResult() {
        return result;
    }
}

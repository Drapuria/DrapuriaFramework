package net.drapuria.framework.util;

import io.github.fastclasspathscanner.ClassInfo;

import java.lang.annotation.Annotation;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FastTypeAnnotationScanner extends ClasspathScanner {

    private final Class<? extends Annotation> annotation;

    private final List<ClassInfo> result = new ArrayList<>();

    public FastTypeAnnotationScanner(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
        scan();
    }

    public FastTypeAnnotationScanner(CodeSource codeSource, Class<? extends Annotation> annotation) {
        super(codeSource, "");
        this.annotation = annotation;
        scan();
    }

    public FastTypeAnnotationScanner(CodeSource codeSource, String packageName, Class<? extends Annotation> annotation) {
        super(codeSource, packageName);
        this.annotation = annotation;
        scan();
    }


    public List<ClassInfo> getResult() {
        return result;
    }

    @Override
    public void queryResult(Collection<ClassInfo> classes) {
        this.result.addAll(classes.stream()
                .filter(classInfo -> classInfo.hasAnnotation(annotation.getName()))
                .collect(Collectors.toList()));
    }
}

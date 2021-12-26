package net.drapuria.framework.util;

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

    @Override
    public void queryResult(Collection<Class<?>> classes) {
        this.result.addAll(classes.stream()
                .filter(aClass -> aClass.isAnnotationPresent(annotation))
                .collect(Collectors.toList()));
    }

    public List<Class<?>> getResult() {
        return this.result;
    }

}

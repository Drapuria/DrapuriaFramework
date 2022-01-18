package net.drapuria.framework.util;


import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.CodeSource;
import java.util.*;


public class MethodAnnotationScanner extends ClasspathScanner {

    @Getter
    private Map<Class<?>, Method[]> results = new HashMap<>();
    private final Class<? extends Annotation> annotation;

    public MethodAnnotationScanner(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
        scan();
    }

    public MethodAnnotationScanner(CodeSource codeSource, Class<? extends Annotation> annotation) {
        super(codeSource, "");
        this.annotation = annotation;
        scan();
    }

    @Override
    public void queryResult(Collection<Class<?>> classes) {
        results = classes.stream()
                .filter(aClass -> Arrays.stream(aClass.getMethods())
                        .anyMatch(method -> method.isAnnotationPresent(annotation)))
                .map(aClass -> {
                    Map<Class<?>, Method[]> map = new HashMap<>();
                    map.put(aClass, Arrays.stream(aClass.getMethods())
                            .filter(method -> method.isAnnotationPresent(annotation)).toArray(Method[]::new));
                    return map;
                })
                .reduce(new HashMap<>(), MethodAnnotationScanner::reduceInto);
    }

    static <K, V> Map<K, V> reduceInto(Map<K, V> reduceInto, Map<K, V> valuesToAdd) {
        reduceInto.putAll(valuesToAdd);
        return reduceInto;
    }

}

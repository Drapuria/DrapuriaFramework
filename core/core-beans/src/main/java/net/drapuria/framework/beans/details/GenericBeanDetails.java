/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.beans.details;

import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import lombok.SneakyThrows;

import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.beans.*;
import net.drapuria.framework.beans.annotation.*;
import net.drapuria.framework.util.Utility;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "unchecked"})
@Getter
@Setter
public class GenericBeanDetails implements BeanDetails {

    private static final Class<? extends Annotation>[] ANNOTATIONS = new Class[] {
            PreInitialize.class, PostInitialize.class,
            PreDestroy.class, PostDestroy.class,
            ShouldInitialize.class
    };

    private String name;

    private ActivationStage stage;
    private Map<Class<? extends Annotation>, String> disallowAnnotations;
    private Map<Class<? extends Annotation>, Collection<Method>> annotatedMethods;

    private AbstractPlugin plugin;

    @Nullable
    private Object instance;
    private Class<?> type;

    private Set<String> children;
    private Map<String, String> tags;

    public GenericBeanDetails(Object instance) {
        this(instance.getClass(), instance, "dummy");
    }

    public GenericBeanDetails(Object instance, Service service) {
        this(instance.getClass(), instance, service.name());
    }

    public GenericBeanDetails(Class<?> type, String name) {
        this.type = type;
        this.name = name;
        this.stage = ActivationStage.NOT_LOADED;
        this.tags = new ConcurrentHashMap<>(0);
        this.children = new HashSet<>();
    }

    public GenericBeanDetails(Class<?> type, @Nullable Object instance, String name) {
        this(type, name);
        this.instance = instance;
        this.loadAnnotations();
    }

    @SneakyThrows
    public void loadAnnotations() {
        this.annotatedMethods = new HashMap<>();
        this.disallowAnnotations = new HashMap<>();

        this.loadAnnotations(Utility.getSuperAndInterfaces(this.type));
    }

    public void loadAnnotations(Collection<Class<?>> superClasses) {
        for (Class<?> type : superClasses) {
            DisallowAnnotation disallowAnnotation = type.getAnnotation(DisallowAnnotation.class);
            if (disallowAnnotation != null) {
                for (Class<? extends Annotation> annotation : disallowAnnotation.value()) {
                    this.disallowAnnotations.put(annotation, type.getName());
                }
            }
        }

        for (Class<?> type : superClasses) {
            if (type.isInterface()) {
                continue;
            }

            for (Method method : type.getDeclaredMethods()) {
                this.loadMethod(method);
            }
        }
    }

    public void loadMethod(Method method) {
        for (Class<? extends Annotation> annotation : ANNOTATIONS) {
            if (method.getAnnotation(annotation) != null) {
                if (this.disallowAnnotations.containsKey(annotation)) {
                    String className = this.disallowAnnotations.get(annotation);
                    throw new IllegalArgumentException("The annotation " + annotation.getSimpleName() + " is disallowed by class " + className + ", But it used in method " + method);
                }

                int parameterCount = method.getParameterCount();
                if (parameterCount > 0) {
                    if (parameterCount != 1 || !BeanDetails.class.isAssignableFrom(method.getParameterTypes()[0])) {
                        throw new IllegalArgumentException("The method " + method + " used annotation " + annotation.getSimpleName() + " but doesn't have matches parameters! you can only use either no parameter or one parameter with ServerData type on annotated " + annotation.getSimpleName() + "!");
                    }
                }

                if (annotation == ShouldInitialize.class && method.getReturnType() != boolean.class) {
                    throw new IllegalArgumentException("The method " + method + " used annotation " + annotation.getSimpleName() + " but doesn't have matches return type! you can only use boolean as return type on annotated " + annotation.getSimpleName() + "!");
                }
                method.setAccessible(true);

                if (this.annotatedMethods.containsKey(annotation)) {
                    this.annotatedMethods.get(annotation).add(method);
                } else {
                    List<Method> methods = new LinkedList<>();
                    methods.add(method);

                    this.annotatedMethods.put(annotation, methods);
                }
                break;
            }
        }
    }

    @Override
    public boolean shouldInitialize() throws InvocationTargetException, IllegalAccessException  {
        if (this.annotatedMethods == null) {
            return true;
        }

        if (instance == null) {
            throw new NullPointerException("The Instance of bean details for " + this.type.getName() + " is null.");
        }

        if (this.annotatedMethods.containsKey(ShouldInitialize.class)) {
            for (Method method : this.annotatedMethods.get(ShouldInitialize.class)) {
                boolean result;

                if (method.getParameterCount() == 1) {
                    result = (boolean) method.invoke(instance, this);
                } else {
                    result = (boolean) method.invoke(instance);
                }

                if (!result) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void call(Class<? extends Annotation> annotation) throws InvocationTargetException, IllegalAccessException {
        if (instance == null) {
            throw new NullPointerException("The Instance of bean details for " + this.type.getName() + " is null.");
        }

        if (this.annotatedMethods.containsKey(annotation)) {
            for (Method method : this.annotatedMethods.get(annotation)) {
                try {
                    if (method.getParameterCount() == 1) {
                        method.invoke(instance, this);
                    } else {
                        method.invoke(instance);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }

        this.changeStage(annotation);
    }

    private void changeStage(Class<? extends Annotation> annotation) {
        if (annotation == PreInitialize.class) {
            this.stage = ActivationStage.PRE_INIT_CALLED;
        } else if (annotation == PostInitialize.class) {
            this.stage = ActivationStage.POST_INIT_CALLED;
        } else if (annotation == PreDestroy.class) {
            this.stage = ActivationStage.PRE_DESTROY_CALLED;
        } else if (annotation == PostDestroy.class) {
            this.stage = ActivationStage.POST_DESTROY_CALLED;
        }
    }

    @Override
    public boolean isStage(ActivationStage stage) {
        return this.stage == stage;
    }

    @Override
    public boolean isActivated() {
        return this.stage == ActivationStage.PRE_INIT_CALLED || this.stage == ActivationStage.POST_INIT_CALLED;
    }

    @Override
    public boolean isDestroyed() {
        return this.stage == ActivationStage.PRE_DESTROY_CALLED || this.stage == ActivationStage.POST_DESTROY_CALLED;
    }

    @Override
    public boolean hasDependencies() {
        return false;
    }

    @Override
    public List<String> getDependencies(ServiceDependencyType type) {
        return Collections.emptyList();
    }

    @Override
    public Set<Map.Entry<ServiceDependencyType, List<String>>> getDependencyEntries() {
        return Collections.emptySet();
    }

    @Override
    @Nullable
    public String getTag(String key) {
        return this.tags.getOrDefault(key, null);
    }

    @Override
    public boolean hasTag(String key) {
        return this.tags.containsKey(key);
    }

    @Override
    public void addTag(String key, String value) {
        this.tags.put(key, value);
    }

    @Override
    public void bindWith(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public AbstractPlugin getBindPlugin() {
        return this.plugin;
    }

    public Set<String> getChildren() {
        return Collections.unmodifiableSet(this.children);
    }

    @Override
    public void addChildren(String children) {
        this.children.add(children);
    }

    @Override
    public void removeChildren(String children) {
        this.children.remove(children);
    }

    @Override
    public boolean isBind() {
        return this.plugin != null;
    }

}

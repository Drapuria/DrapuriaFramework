package net.drapuria.framework.services.details;

import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.services.ServiceDependencyType;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface BeanDetails {

    boolean shouldInitialize() throws InvocationTargetException, IllegalAccessException;

    void call(Class<? extends Annotation> annotation) throws InvocationTargetException, IllegalAccessException;

    boolean isStage(ActivationStage stage);

    boolean isActivated();

    boolean isDestroyed();

    @Nullable
    String getTag(String key);

    boolean hasTag(String key);

    void addTag(String key, String value);

    void setName(String name);

    void setStage(ActivationStage stage);

    void setDisallowAnnotations(java.util.Map<Class<? extends Annotation>, String> disallowAnnotations);

    void setAnnotatedMethods(java.util.Map<Class<? extends Annotation>, java.util.Collection<Method>> annotatedMethods);

    void setInstance(Object instance);

    void setType(Class<?> type);

    void setTags(java.util.Map<String, String> tags);

    String getName();

    ActivationStage getStage();

    Map<Class<? extends Annotation>, String> getDisallowAnnotations();

    Map<Class<? extends Annotation>, java.util.Collection<Method>> getAnnotatedMethods();

    @Nullable
    Object getInstance();

    Class<?> getType();

    Map<String, String> getTags();

    void bindWith(AbstractPlugin plugin);

    AbstractPlugin getBindPlugin();

    boolean isBind();

    boolean hasDependencies();

    Set<String> getChildren();

    void addChildren(String children);

    void removeChildren(String children);

    List<String> getDependencies(ServiceDependencyType type);

    Set<Map.Entry<ServiceDependencyType, List<String>>> getDependencyEntries();

    default Set<String> getAllDependencies() {
        return this.getDependencyEntries().stream()
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    default void onEnable() {

    }

    default void onDisable() {

    }

    enum ActivationStage {

        NOT_LOADED,
        PRE_INIT_CALLED,
        POST_INIT_CALLED,

        PRE_DESTROY_CALLED,
        POST_DESTROY_CALLED

    }

}

/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.beans.details;

import com.google.common.collect.Lists;

import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.beans.ServiceDependencyType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class DependenciesBeanDetails extends GenericBeanDetails {

    protected final Map<ServiceDependencyType, List<String>> dependencies;

    public DependenciesBeanDetails(Object instance) {
        this(instance.getClass(), instance, "dummy");
    }

    public DependenciesBeanDetails(Object instance, Service service) {
        this(instance.getClass(), instance, service.name());
    }

    public DependenciesBeanDetails(Class<?> type, String name) {
        super(type, name);
        this.dependencies = new HashMap<>();
        for (ServiceDependencyType dependencyType : ServiceDependencyType.values()) {
            this.dependencies.put(dependencyType, Lists.newArrayList());
        }
    }

    public DependenciesBeanDetails(Class<?> type, String name, String[] dependencies) {
        this(type, name);
        this.addDependencies(ServiceDependencyType.FORCE, dependencies);
    }

    public DependenciesBeanDetails(Class<?> type, @Nullable Object instance, String name) {
        super(type, instance, name);
        this.dependencies = new HashMap<>();
        for (ServiceDependencyType dependencyType : ServiceDependencyType.values()) {
            this.dependencies.put(dependencyType, Lists.newArrayList());
        }
    }

    public DependenciesBeanDetails(Class<?> type, @Nullable Object instance, String name, String[] dependencies) {
        this(type, instance, name);
        this.addDependencies(ServiceDependencyType.FORCE, dependencies);
    }

    public void addDependencies(ServiceDependencyType type, String... dependencies) {
        for (String dependency : dependencies) {
            this.getDependencies(type).add(dependency);
        }
    }

    public List<String> getDependencies(ServiceDependencyType type) {
        return this.dependencies.get(type);
    }

    public Set<Map.Entry<ServiceDependencyType, List<String>>> getDependencyEntries() {
        return this.dependencies.entrySet();
    }

    @Override
    public boolean hasDependencies() {
        return this.dependencies.size() > 0;
    }
}

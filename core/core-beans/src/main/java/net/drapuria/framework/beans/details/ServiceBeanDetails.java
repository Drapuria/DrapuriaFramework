/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.beans.details;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.beans.annotation.DependencyType;
import net.drapuria.framework.beans.annotation.ServiceDependency;
import net.drapuria.framework.beans.ServiceDependencyType;
import net.drapuria.framework.beans.details.constructor.BeanParameterDetailsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Collection;

@Getter
@Setter
public class ServiceBeanDetails extends DependenciesBeanDetails {

    private BeanParameterDetailsConstructor constructorDetails;

    public ServiceBeanDetails(Class<?> type, String name, String[] dependencies) {
        super(type, name, dependencies);

        this.loadAnnotations();
    }

    public void setupConstruction(BeanContext beanContext) {
        this.constructorDetails = new BeanParameterDetailsConstructor(this.getType(), beanContext);
        for (Parameter parameter : this.constructorDetails.getParameters()) {
            BeanDetails details = beanContext.getBeanDetails(parameter.getType());

            ServiceDependencyType type = ServiceDependencyType.FORCE;
            final DependencyType annotation = parameter.getAnnotation(DependencyType.class);
            if (annotation != null) {
                type = annotation.value();
            }
            this.addDependencies(type, details.getName());
        }
    }

    public void build(BeanContext context) {
        if (this.constructorDetails == null) {
            throw new IllegalArgumentException("The construction for bean details " + this.getType().getName() + " hasn't been called!");
        }

        try {
            this.setInstance(this.constructorDetails.newInstance(context));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadAnnotations(Collection<Class<?>> superClasses) {
        super.loadAnnotations(superClasses);

        for (Class<?> type : superClasses) {
            for (ServiceDependency dependency : type.getAnnotationsByType(ServiceDependency.class)) {
                this.addDependencies(dependency.type().value(), dependency.dependencies());
            }
        }
    }
}

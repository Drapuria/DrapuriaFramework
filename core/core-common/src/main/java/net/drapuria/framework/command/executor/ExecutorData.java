package net.drapuria.framework.command.executor;

import lombok.Getter;
import net.drapuria.framework.command.context.ParsedArgument;
import net.drapuria.framework.command.context.permission.PermissionContext;
import net.drapuria.framework.command.parameter.Parameter;
import net.drapuria.framework.command.parameter.ParameterData;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public abstract class ExecutorData<E, P extends Parameter> {

    private final Method method;
    private final PermissionContext<E> permissionContext;
    private final String permission;
    private final String name;
    private final List<String> aliases;
    private final ParameterData<P> parameterData;
    private final int aliasSize;
    protected Class<?> accessibleFor;


    protected ExecutorData(Method method, PermissionContext<E> permissionContext, String permission,
                           Set<String> aliases, ParameterData<P> parameterData, String name) {
        this.method = method;
        this.permissionContext = permissionContext;
        this.permission = permission;
        this.aliases = new ArrayList<>(aliases);
        this.parameterData = parameterData;
        this.aliasSize = this.aliases.size();
        if (method != null && Modifier.isPrivate(method.getModifiers())) {
            method.setAccessible(true);
        }
        if (method != null) {
            this.accessibleFor = this.method.getParameterTypes()[0];
        } else this.accessibleFor = null;
        this.aliases.replaceAll(String::toLowerCase);
        this.name = name;
    }

    public boolean canAccess(E e) {
        if (this.accessibleFor == null || !accessibleFor.isAssignableFrom(e.getClass()))
            return false;
        return permissionContext.hasPermission(e, permission);
    }

    public boolean isValidLabel(String label) {
        if (this.aliasSize == 0)
            return true;
        return this.aliases.contains(label);
    }

    public abstract ParsedArgument<?>[] getParsedArguments(E source, String[] contextArguments);

}

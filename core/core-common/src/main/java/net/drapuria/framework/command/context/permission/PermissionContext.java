package net.drapuria.framework.command.context.permission;

public interface PermissionContext<T> {

    boolean hasPermission(final T executor);

}

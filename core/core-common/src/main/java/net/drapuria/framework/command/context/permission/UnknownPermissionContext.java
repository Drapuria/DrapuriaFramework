package net.drapuria.framework.command.context.permission;

public class UnknownPermissionContext<T> implements PermissionContext<T> {

    @Override
    public boolean hasPermission(T executor, String permission) {
        return true;
    }
}
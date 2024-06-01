package net.drapuria.framework.bukkit.impl.command.context.permission;

import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.command.annotation.DefaultPermissionContext;
import net.drapuria.framework.command.context.permission.PermissionContext;
import org.bukkit.command.CommandSender;

@DefaultPermissionContext
@Component
public class BukkitPermissionContext implements PermissionContext<CommandSender> {

    @Override
    public boolean hasPermission(CommandSender executor, String permission) {
        if ("".equalsIgnoreCase(permission))
            return true;
        return executor.hasPermission(permission);
    }
}

package net.drapuria.framework.bukkit.impl.command.executor;

import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.type.CommandTypeParameter;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.command.context.ParsedArgument;
import net.drapuria.framework.command.context.permission.PermissionContext;
import net.drapuria.framework.command.executor.ExecutorData;
import net.drapuria.framework.command.parameter.ParameterData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class BukkitExecutorData extends ExecutorData<CommandSender, BukkitParameter> {

    public BukkitExecutorData(Method method, PermissionContext<CommandSender> permissionContext, String permission,
                              Set<String> aliases, ParameterData<BukkitParameter> parameterData, String name) {
        super(method, permissionContext, permission, aliases, parameterData, name);
        if (accessibleFor == DrapuriaPlayer.class) {
            accessibleFor = Player.class;
        }
    }

    public ParsedArgument<?>[] getParsedArguments(final CommandSender executor, final String[] params) {
        final boolean isPlayer = executor instanceof Player;
        final Player bukkitPlayer = isPlayer ? (Player) executor : null;
        final ParsedArgument<?>[] parsedArguments = new ParsedArgument[super.getParameterData().getParameterCount()];
        for (int i = 0; i < super.getParameterData().getParameterCount(); i++) {
            final BukkitParameter parameter = super.getParameterData().get(i);
            String current = params.length == i ? parameter.getDefaultValue().isEmpty() ? null : parameter.getDefaultValue() : params[i];
            if (current == null)
                return parsedArguments;
            final CommandTypeParameter<?> typeParameter = Drapuria.getCommandProvider.getTypeParameter(parameter.getClassType());
            if (typeParameter == null)
                return parsedArguments;
            final Object parsedObject;
            if (params.length > i && i == super.getParameterData().getParameterCount() - 1 &&
                    parameter.getClassType().equals(String.class) /* wild cards only for strings */ &&
                    parameter.isWildcard()) {
                current = String.join(" ", Arrays.copyOfRange(params, i, params.length));
            }
            if (isPlayer) {
                parsedObject = typeParameter.parse(bukkitPlayer, current);
            } else {
                parsedObject = typeParameter.parseNonPlayer(executor, current);
            }
            if (parsedObject == null)
                return parsedArguments;
            parsedArguments[i] = new ParsedArgument<>(i, parsedObject, parameter.isWildcard());
        }
        return parsedArguments;
    }
}

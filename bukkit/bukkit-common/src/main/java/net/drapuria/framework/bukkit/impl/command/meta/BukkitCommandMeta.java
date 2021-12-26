package net.drapuria.framework.bukkit.impl.command.meta;

import lombok.Getter;
import net.drapuria.framework.bukkit.impl.command.DrapuriaCommand;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameter;
import net.drapuria.framework.bukkit.impl.command.parameter.BukkitParameterData;
import net.drapuria.framework.command.annotations.Command;
import net.drapuria.framework.command.annotations.CommandParameter;
import net.drapuria.framework.command.annotations.SubCommand;
import net.drapuria.framework.command.meta.CommandMeta;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class BukkitCommandMeta extends CommandMeta<Player> {

    private String commandPermission;

    private DrapuriaCommand parent;
    private final Map<String, List<String>> activeAliases;
    private final Map<String, BukkitSubCommandMeta> subCommandMeta;
    private boolean isUseUnlySubCommands;

    public BukkitCommandMeta(DrapuriaCommand parent) {
        super(parent.getName(), null);
        this.parent = parent;
        this.activeAliases = new HashMap<>();

        this.fetchCommands();
        this.subCommandMeta = this.fetchSubCommands();
    }

    public void setCommandPermission(String commandPermission) {
        this.commandPermission = commandPermission;
    }

    public BukkitSubCommandMeta getSubCommandMeta(String input) {
        return this.activeAliases.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(input))
                .findFirst()
                .map(entry -> this.subCommandMeta.get(entry.getKey()))
                .orElse(null);
    }


    private void fetchCommands() {
        Command command = this.parent.getClass().getAnnotation(Command.class);

        if (command == null) return;

        this.commandPermission = command.permission();
        this.commandDescription = command.description();
        this.commandAliases = command.names();
        this.asyncExecution = command.async();
        this.isUseUnlySubCommands = command.useSubCommandsOnly();

        this.commandName = this.commandAliases[0];
    }

    private Map<String, BukkitSubCommandMeta> fetchSubCommands() {
        Map<String, BukkitSubCommandMeta> tmpHashMap = new HashMap<>();
        Method[] methods = this.parent.getClass().getDeclaredMethods();

        for (Method method : methods)
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand subCommand = method.getAnnotation(SubCommand.class);

                Class<?>[] parameterTypes = method.getParameterTypes();
                String[] annotationParameterTypes = StringUtils.substringsBetween(subCommand.parameters(), "{", "}");

                if (parameterTypes.length == 0) break;

                BukkitParameter[] parameters = new BukkitParameter[parameterTypes.length - 1];

                for (int i = 1; i < parameterTypes.length; i++) {
                    Class<?> parameter = method.getParameterTypes()[i];
                    if (method.getParameters()[i].isAnnotationPresent(CommandParameter.class)) {
                        CommandParameter parameterInfo = method.getParameters()[i].getAnnotation(CommandParameter.class);
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                parameterInfo.defaultValue(),
                                parameterInfo.wildcard(),
                                parameterInfo.tabCompleteFlags());

                    } else {
                        parameters[i - 1] = new BukkitParameter(parameter,
                                annotationParameterTypes[i - 1],
                                "",
                                false,
                                new String[]{});
                    }
                }

                BukkitParameterData parameterData = new BukkitParameterData(parameters);
                BukkitSubCommandMeta meta = new BukkitSubCommandMeta(subCommand, parameterData, this.parent, method);

                String defaultAlias = meta.getDefaultAlias();

                tmpHashMap.put(defaultAlias, meta);
                this.activeAliases.put(defaultAlias, Arrays.asList(meta.getAliases()));
            }

        return tmpHashMap;
    }

    @Override
    public boolean canAccess(Player executor) {
        if ("".equalsIgnoreCase(this.commandPermission))
            return true;
        return executor.hasPermission(this.commandPermission);
    }
}

/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.command.parameter;

import lombok.Getter;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.LocalizedMessage;
import net.drapuria.framework.bukkit.player.MessageShowType;
import net.drapuria.framework.command.annotation.NullParameterAction;
import net.drapuria.framework.command.parameter.Parameter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
public class BukkitParameter extends Parameter<CommandSender> {
    private final String[] tabCompleteFlags;
    private Method nullParameterMethod = null;
    private String errorString;
    private final Object commandObject;
    private MessageShowType messageShowType = MessageShowType.ACTION_BAR;
    private String translateableErrorString;

    public BukkitParameter(Class<?> classType, String parameter, String defaultValue, boolean wildcard,
                           boolean isAllowNull, String[] tabCompleteFlags, java.lang.reflect.Parameter javaParameter,
                           final Class<?> commandClass, final Object commandObject,
                           final NullParameterAction nullParameterAction) {
        super(classType, parameter, defaultValue, wildcard, isAllowNull, javaParameter, nullParameterAction);
        this.tabCompleteFlags = tabCompleteFlags;
        this.commandObject = commandObject;
        if (nullParameterAction == null)
            return;
        if (!nullParameterAction.method().isEmpty()) {
            for (Method declaredMethod : commandClass.getDeclaredMethods()) {
                if (declaredMethod.getName().equals(nullParameterAction.method())) {
                    this.nullParameterMethod = declaredMethod;
                    this.nullParameterMethod.setAccessible(true);
                    break;
                }
            }
        } else if (!nullParameterAction.errorString().isEmpty()) {
            this.errorString = nullParameterAction.errorString();
            if (this.errorString.startsWith("::")) {
                try {
                    this.messageShowType = MessageShowType.valueOf(this.errorString.split("::")[1]);
                    this.errorString = this.errorString.split("::")[2];
                } catch (Exception ignored) {

                }
            }
        } else if (!nullParameterAction.translateableErroString().isEmpty()) {
            this.translateableErrorString = nullParameterAction.translateableErroString();
            if (this.translateableErrorString.startsWith("::")) {
                try {
                    this.messageShowType = MessageShowType.valueOf(this.translateableErrorString.split("::")[1]);
                    this.translateableErrorString = this.translateableErrorString.split("::")[2];
                } catch (Exception ignored) {

                }
            }
        }
    }

    @Override
    public boolean handleNullParameterAction(CommandSender commandSender) {
        if (nullParameterMethod != null) {
            try {
                nullParameterMethod.invoke(commandObject, commandSender);
                return true;
            } catch (IllegalAccessException | InvocationTargetException ignored) {
                return false;
            }
        }
        if (errorString != null) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                switch (messageShowType) {
                    case CHAT:
                        player.sendMessage(errorString);
                        break;
                    case TITLE:
                        player.sendTitle(new Title.Builder().title(errorString).fadeIn(5).stay(20 * 3).fadeOut(20).build());
                        break;
                    case SUBTITLE:
                        player.sendTitle(new Title.Builder().subtitle(errorString).fadeIn(5).stay(20 * 3).fadeOut(20).build());
                        break;
                    case ACTION_BAR:
                        Drapuria.IMPLEMENTATION.sendActionBar(player, errorString);
                        break;
                }
            } else commandSender.sendMessage(errorString);
            return true;
        } else if (translateableErrorString != null) {
            LocalizedMessage message = LocalizedMessage.of(translateableErrorString)
                    .showType(messageShowType);
            if (commandSender instanceof Player)
                message.send((Player) commandSender);
            else
                commandSender.sendMessage(message.getMessage());
            return true;
        }
        return false;
    }
}

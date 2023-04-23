package net.drapuria.framework.bukkit.impl.command.parameter.type;

import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GamemodeTypeParameter extends CommandTypeParameter<GameMode> {
    @Override
    public GameMode parseNonPlayer(CommandSender sender, String value) {
        try {
            return GameMode.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return Arrays.stream(GameMode.values()).map(gameMode -> gameMode.name().toLowerCase())
                .filter(name -> name.toLowerCase().startsWith(source)).collect(Collectors.toList());
    }

    @Override
    public GameMode parse(Player sender, String source) {

        try {
            return GameMode.valueOf(source.toUpperCase());
        } catch (Exception ignored) {
            try {
                int number = Integer.parseInt(source);
                return GameMode.getByValue(number);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    @Override
    public Class<GameMode> getType() {
        return GameMode.class;
    }
}

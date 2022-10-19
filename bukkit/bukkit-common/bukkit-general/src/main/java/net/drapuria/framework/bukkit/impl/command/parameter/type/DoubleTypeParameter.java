package net.drapuria.framework.bukkit.impl.command.parameter.type;

import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class DoubleTypeParameter extends CommandTypeParameter<Double>{
    @Override
    public Double parseNonPlayer(CommandSender sender, String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return Collections.emptyList();
    }

    @Override
    public Double parse(Player sender, String source) {
        return parseNonPlayer(sender, source);
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}

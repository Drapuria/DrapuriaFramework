package net.drapuria.framework.bukkit.fake.hologram.helper;

import com.comphenix.protocol.utility.MinecraftReflection;
import lombok.experimental.UtilityClass;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.minecraft.MinecraftVersion;
import net.drapuria.framework.bukkit.reflection.version.PlayerVersion;
import org.bukkit.entity.Player;

@UtilityClass
public class HologramOffsets {

    private boolean enabled = false;

    public double getOffset(PlayerVersion version) {
        return 1.0D;
    }

    public double getOffset(final Player player) {
        if (!enabled)
            return 1.0D;
        return getOffset(Minecraft.getProtocol(player));
    }
}

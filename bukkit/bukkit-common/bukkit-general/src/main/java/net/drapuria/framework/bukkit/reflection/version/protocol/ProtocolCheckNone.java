package net.drapuria.framework.bukkit.reflection.version.protocol;

import net.drapuria.framework.bukkit.reflection.annotation.ProtocolImpl;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import org.bukkit.entity.Player;

@ProtocolImpl
public class ProtocolCheckNone implements ProtocolCheck {
    @Override
    public int getVersion(Player player) {
        return Minecraft.MINECRAFT_VERSION.version();
    }
}

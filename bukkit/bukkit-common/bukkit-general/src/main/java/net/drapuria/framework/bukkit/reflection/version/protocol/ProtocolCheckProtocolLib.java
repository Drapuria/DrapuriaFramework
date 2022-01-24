package net.drapuria.framework.bukkit.reflection.version.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import net.drapuria.framework.bukkit.impl.annotation.ProviderTestImpl;
import net.drapuria.framework.bukkit.impl.test.ImplementationTest;
import net.drapuria.framework.bukkit.reflection.annotation.ProtocolImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


@ProtocolImpl
@ProviderTestImpl(ProtocolCheckProtocolLib.TestImpl.class)
public class ProtocolCheckProtocolLib implements ProtocolCheck {
    @Override
    public int getVersion(Player player) {
        return ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
    }

    public static class TestImpl implements ImplementationTest {

        @Override
        public boolean test() {
            return Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;
        }
    }
}

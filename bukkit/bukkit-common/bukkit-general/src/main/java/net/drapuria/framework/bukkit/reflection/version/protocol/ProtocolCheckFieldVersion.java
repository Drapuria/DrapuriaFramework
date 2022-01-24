package net.drapuria.framework.bukkit.reflection.version.protocol;

import net.drapuria.framework.bukkit.impl.annotation.ProviderTestImpl;
import net.drapuria.framework.bukkit.impl.test.ImplementationTest;
import net.drapuria.framework.bukkit.reflection.annotation.ProtocolImpl;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;
import net.drapuria.framework.bukkit.reflection.resolver.ResolverQuery;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.NMSClassResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.FieldWrapper;
import org.bukkit.entity.Player;


import java.lang.reflect.Field;

@ProtocolImpl
@ProviderTestImpl(ProtocolCheckFieldVersion.TestImpl.class)
public class ProtocolCheckFieldVersion implements ProtocolCheck {

    private FieldWrapper PLAYER_CONNECTION_FIELD;
    private FieldWrapper NETWORK_MANAGER_FIELD;
    private FieldWrapper VERSION_FIELD;

    public ProtocolCheckFieldVersion() {
        NMSClassResolver nmsClassResolver = new NMSClassResolver();
        try {
            Class<?> networkManager = nmsClassResolver.resolve("NetworkManager");
            Class<?> playerConnection = nmsClassResolver.resolve("PlayerConnection");
            Class<?> entityPlayer = nmsClassResolver.resolve("EntityPlayer");

            FieldResolver fieldResolver = new FieldResolver(entityPlayer);

            PLAYER_CONNECTION_FIELD = fieldResolver.resolveByFirstTypeWrapper(playerConnection);

            fieldResolver = new FieldResolver(playerConnection);

            NETWORK_MANAGER_FIELD = fieldResolver.resolveByFirstTypeWrapper(networkManager);

            fieldResolver = new FieldResolver(networkManager);
            VERSION_FIELD = fieldResolver.resolveWrapper("version");
        } catch (ReflectiveOperationException e) {
        }
    }

    @Override
    public int getVersion(Player player) {
        Object entityPlayer = Minecraft.getHandleSilent(player);
        Object playerConnection = PLAYER_CONNECTION_FIELD.get(entityPlayer);
        Object networkManager = NETWORK_MANAGER_FIELD.get(playerConnection);

        return (int) VERSION_FIELD.get(networkManager);
    }

    public static class TestImpl implements ImplementationTest {

        @Override
        public boolean test() {
            Class<?> networkManager;


            try {
                NMSClassResolver classResolver = new NMSClassResolver();
                networkManager = classResolver.resolve("NetworkManager");
            } catch (Throwable throwable) {
                return false;
            }

            try {
                FieldResolver resolver = new FieldResolver(networkManager);
                Field field = resolver.resolve(new ResolverQuery("version", Integer.class, int.class));

                return field != null;
            } catch (Throwable throwable) {
            }

            return false;
        }
    }
}

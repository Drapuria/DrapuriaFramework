package net.drapuria.framework.bukkit.reflection.version.protocol;

import net.drapuria.framework.bukkit.impl.annotation.ProviderTestImpl;
import net.drapuria.framework.bukkit.impl.test.ImplementationTest;
import net.drapuria.framework.bukkit.reflection.annotation.ProtocolImpl;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;
import net.drapuria.framework.bukkit.reflection.resolver.MethodResolver;
import net.drapuria.framework.bukkit.reflection.resolver.ResolverQuery;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.NMSClassResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.FieldWrapper;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.MethodWrapper;
import org.bukkit.entity.Player;


import java.lang.reflect.Method;

@ProtocolImpl
@ProviderTestImpl(ProtocolCheckMethodVersion.TestImpl.class)
public class ProtocolCheckMethodVersion implements ProtocolCheck {

    private FieldWrapper PLAYER_CONNECTION_FIELD;
    private FieldWrapper NETWORK_MANAGER_FIELD;
    private MethodWrapper GET_VERSION_METHOD;

    public ProtocolCheckMethodVersion() {
        NMSClassResolver nmsClassResolver = new NMSClassResolver();
        try {
            Class<?> networkManager = nmsClassResolver.resolve("NetworkManager");
            Class<?> playerConnection = nmsClassResolver.resolve("PlayerConnection");
            Class<?> entityPlayer = nmsClassResolver.resolve("EntityPlayer");

            FieldResolver fieldResolver = new FieldResolver(entityPlayer);

            PLAYER_CONNECTION_FIELD = fieldResolver.resolveByFirstTypeWrapper(playerConnection);

            fieldResolver = new FieldResolver(playerConnection);

            NETWORK_MANAGER_FIELD = fieldResolver.resolveByFirstTypeWrapper(networkManager);

            MethodResolver resolver = new MethodResolver(networkManager);
            GET_VERSION_METHOD = resolver.resolveWrapper(new ResolverQuery("getVersion", int.class));
        } catch (ReflectiveOperationException e) {
            PLAYER_CONNECTION_FIELD = null;
            NETWORK_MANAGER_FIELD = null;
            GET_VERSION_METHOD = null;
        }
    }

    @Override
    public int getVersion(Player player) {
        Object entityPlayer = Minecraft.getHandleSilent(player);
        Object playerConnection = PLAYER_CONNECTION_FIELD.get(entityPlayer);
        Object networkManager = NETWORK_MANAGER_FIELD.get(playerConnection);

        return (int) GET_VERSION_METHOD.invoke(networkManager);
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
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN
                // TODO REFLECTION WIEDER AUSTAUSCHEN MIT ORGINALEN DANN SOLLTE ALLES GEHEN 
                MethodResolver resolver = new MethodResolver(networkManager);

                Method method = resolver.resolve(new ResolverQuery("getVersion", int.class));

                return method != null && (method.getReturnType() == int.class || method.getReturnType() == Integer.class);
            } catch (Throwable throwable) {
            }

            return false;
        }
    }
}

package net.drapuria.framework.bukkit.reflection.resolver.wrapper;

import com.google.common.collect.Multimap;
import lombok.Getter;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.resolver.FieldResolver;
import net.drapuria.framework.bukkit.reflection.resolver.minecraft.NMSClassResolver;
import net.drapuria.framework.bukkit.reflection.resolver.wrapper.impl.GameProfileImplementation;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GameProfileWrapper extends WrapperAbstract {

    public static final GameProfileImplementation IMPLEMENTATION = GameProfileImplementation.getImplementation();

    private static FieldWrapper<?> GAME_PROFILE_FIELD;

    static {
        try {
            NMSClassResolver classResolver = new NMSClassResolver();
            Class<?> entityHumanClass = classResolver.resolve("EntityHuman", "world.entity.player.EntityHuman");

            GAME_PROFILE_FIELD = new FieldResolver(entityHumanClass)
                    .resolveByFirstTypeWrapper(GameProfileWrapper.IMPLEMENTATION.getGameProfileClass());
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Getter
    protected Object handle;
    private Multimap<String, SignedPropertyWrapper> propertyMap;

    public GameProfileWrapper(Object handle) {
        this.handle = handle;
    }

    public GameProfileWrapper(UUID uuid, String name) {
        this(IMPLEMENTATION.create(name, uuid));
    }

    public static GameProfileWrapper fromPlayer(Player player) {
        if (GAME_PROFILE_FIELD == null) {
            try {
                NMSClassResolver classResolver = new NMSClassResolver();
                Class<?> entityHumanClass = classResolver.resolve("EntityHuman", "world.entity.player.EntityHuman");

                GAME_PROFILE_FIELD = new FieldResolver(entityHumanClass)
                        .resolveByFirstTypeWrapper(GameProfileWrapper.IMPLEMENTATION.getGameProfileClass());
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        Object nmsPlayer = Minecraft.getHandleSilent(player);
        return new GameProfileWrapper(GAME_PROFILE_FIELD.get(nmsPlayer));
    }

    public String getName() {
        return IMPLEMENTATION.getName(handle);
    }

    public UUID getUuid() {
        return IMPLEMENTATION.getUuid(handle);
    }

    public void setName(String name) {
        IMPLEMENTATION.setName(handle, name);
    }

    public void setUuid(UUID uuid) {
        IMPLEMENTATION.setUuid(handle, uuid);
    }

    public Multimap<String, SignedPropertyWrapper> getProperties() {
        Multimap<String, SignedPropertyWrapper> result = this.propertyMap;

        if (result == null) {
            this.propertyMap = result = IMPLEMENTATION.getProperties(handle);
        }

        return result;
    }

    @Override
    public boolean exists() {
        return handle != null;
    }
}

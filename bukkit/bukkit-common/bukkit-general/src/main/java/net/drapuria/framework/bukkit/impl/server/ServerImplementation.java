/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.server;

import lombok.SneakyThrows;
import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.bukkit.impl.annotation.ProviderTestImpl;
import net.drapuria.framework.bukkit.impl.annotation.ServerImpl;
import net.drapuria.framework.bukkit.impl.test.ImplementationFactory;
import net.drapuria.framework.bukkit.inventory.anvil.AbstractVirtualAnvil;
import net.drapuria.framework.bukkit.inventory.anvil.ConfirmAction;
import net.drapuria.framework.bukkit.util.BlockPosition;
import net.drapuria.framework.bukkit.util.Skin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.imanity.framework.reflect.ReflectLookup;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;


public interface ServerImplementation {

    @SneakyThrows
    static ServerImplementation load(BeanContext beanContext) {
        ReflectLookup reflectLookup = new ReflectLookup(Collections.singleton(ServerImplementation.class.getClassLoader()),
                Collections.singleton("net.drapuria.framework"));
        Class<?> lastSuccess = null;
        lookup:
        for (Class<?> type : reflectLookup.findAnnotatedClasses(ServerImpl.class)) {
            if (!ServerImplementation.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException("The type " + type.getName() + " does not implement to ProtocolCheck!");
            }

            ImplementationFactory.TestResult result = ImplementationFactory.test(type.getAnnotation(ProviderTestImpl.class));
            switch (result) {
                case NO_PROVIDER:
                    lastSuccess = type;
                    break;
                case SUCCESS:
                    lastSuccess = type;
                    break lookup;
                case FAILURE:
                    break;
            }
        }

        if (lastSuccess == null) {
            throw new UnsupportedOperationException("Couldn't find any usable protocol check! (but it shouldn't be possible)");
        }
        return (ServerImplementation) lastSuccess.newInstance();
    }

    Entity getEntity(UUID uuid);

    Entity getEntity(World world, int id);

    default Entity getEntity(int id) {
        return Bukkit.getWorlds().stream()
                .map(world -> this.getEntity(world, id)).filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    Object toBlockNMS(MaterialData materialData);

    List<Player> getPlayersInRadius(Location location, double radius);

    void setFakeBlocks(Player player, Map<BlockPosition, MaterialData> positions, List<BlockPosition> toRemove, boolean send);

    void clearFakeBlocks(Player player, boolean send);

    void sendActionBar(Player player, String message);

    void sendTitle(Player player, String message, int fadeIn, int showTime, int fadeOut);

    void sendSubTitle(Player player, String message, int fadeIn, int showTime, int fadeOut);

    boolean isServerThread();

    boolean callMoveEvent(Player player, Location from, Location to);

    Skin getSkinFromPlayer(Player player);

    AbstractVirtualAnvil createVirtualAnvil(Player player, BiConsumer<Player, ConfirmAction> onCancel, BiFunction<Player, String, ConfirmAction> confirm);

    AbstractVirtualAnvil createVirtualAnvil(Player player, String startText, BiConsumer<Player, ConfirmAction> onCancel, BiFunction<Player, String, ConfirmAction> confirm);

    AbstractVirtualAnvil createVirtualAnvil(Player player, String startText, ItemStack displayItem, BiConsumer<Player, ConfirmAction> onCancel, BiFunction<Player, String, ConfirmAction> confirm);


}

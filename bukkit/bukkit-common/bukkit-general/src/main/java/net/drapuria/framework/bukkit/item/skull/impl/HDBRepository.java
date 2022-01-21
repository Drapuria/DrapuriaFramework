/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.item.skull.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.bukkit.item.skull.SkullRepository;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service(name = "hdbRepository")
public class HDBRepository extends SkullRepository {

    private static Object apiInstance;
    public static Method apiGetHeadMethod;

    static {
        try {
            Class<?> apiClass = Class.forName("me.arcaniax.hdb.api.HeadDatabaseAPI");
            apiInstance = apiClass.newInstance();
            apiGetHeadMethod = apiClass.getDeclaredMethod("getItemHead", String.class);
        } catch (Exception ignored) {
            apiGetHeadMethod = null;
        }
    }

    @Getter
    @Setter
    private boolean enabled;

    private final List<String> queuedIds = new ArrayList<>();

    /**
     * Searches for the cached skull, if found it will use the found skull, if not it will search the skull by its id
     *
     * @param id head id
     * @return skull item
     */
    @SneakyThrows
    @Override
    public Optional<ItemStack> findById(String id) {
        if (id == null)
            return Optional.empty();
        Optional<ItemStack> optional = super.findById(id);
        if (optional.isPresent())
            return optional;
        else {
            ItemStack itemStack = (ItemStack) apiGetHeadMethod.invoke(apiInstance, id);
            if (itemStack == null) {
                if (!this.enabled)
                    queuedIds.add(id);
                return Optional.empty();
            }
            try {
                return Optional.ofNullable((ItemStack) apiGetHeadMethod.invoke(apiInstance, id));
            } catch (IllegalAccessException | InvocationTargetException e) {
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<ItemStack> findBy(String field, Object key) {
        throw new UnsupportedOperationException("Cannot find skull by field");
    }

    public void processQueuedIds() {
        queuedIds.forEach(s -> {
            ItemStack itemStack;
            try {
                itemStack = (ItemStack) apiGetHeadMethod.invoke(apiInstance, s);
                if (itemStack != null)
                    HDBRepository.super.storage.put(s, itemStack);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        });
    }

}

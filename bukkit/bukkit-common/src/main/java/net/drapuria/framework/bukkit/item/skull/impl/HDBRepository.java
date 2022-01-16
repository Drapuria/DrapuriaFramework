package net.drapuria.framework.bukkit.item.skull.impl;

import lombok.Getter;
import lombok.Setter;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.bukkit.item.skull.SkullRepository;
import net.drapuria.framework.database.SqlService;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service(name = "hdbRepository")
public class HDBRepository extends SkullRepository {

    private static HeadDatabaseAPI api;

    static {
        try {
            Class.forName("me.arcaniax.hdb.api.HeadDatabaseAPI");
            api = new HeadDatabaseAPI();
        } catch (Exception ignored) {
            api = null;
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
    @Override
    public Optional<ItemStack> findById(String id) {
        if (id == null)
            return Optional.empty();
        Optional<ItemStack> optional = super.findById(id);
        if (optional.isPresent())
            return optional;
        else {
            ItemStack itemStack = api.getItemHead(id);
            if (itemStack == null) {
                if (!this.enabled)
                    queuedIds.add(id);
                return Optional.empty();
            }
            return Optional.ofNullable(api.getItemHead(id));
        }
    }

    @Override
    public Optional<ItemStack> findBy(String field, Object key) {
        throw new UnsupportedOperationException("Cannot find skull by field");
    }

    public void processQueuedIds() {
        queuedIds.forEach(s -> {
            final ItemStack itemStack = api.getItemHead(s);
            if (itemStack != null)
                HDBRepository.super.storage.put(s, itemStack);
        });
    }

}

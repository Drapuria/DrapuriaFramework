package net.drapuria.framework.bukkit.fake.entity;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.drapuria.framework.bukkit.fake.entity.living.LivingFakeEntity;
import net.drapuria.framework.bukkit.fake.entity.living.VillagerFakeEntity;
import net.drapuria.framework.bukkit.fake.entity.npc.NPC;
import net.drapuria.framework.bukkit.fake.entity.npc.NPCOptions;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

@Setter
@Accessors(chain = true, fluent = true)
public class FakeEntityFactory {

    private EntityType entityType;
    private FakeEntityOptions options;
    private Location location;
    private FakeEntityPool entityPool;
    private NPCOptions npcOptions;

    @SuppressWarnings("unchecked")
    public <T extends FakeEntity> T create() {
        if (entityType == null) {
            throw new IllegalArgumentException("Entity type must be set");
        }
        if (options == null) {
            throw new IllegalArgumentException("Options must be set");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location must be set");
        }
        if (entityPool == null) {
            throw new IllegalArgumentException("Entity pool must be set");
        }
        switch (entityType) {
            case VILLAGER:
                return (T) new VillagerFakeEntity(FakeEntityService.getService.getFreeEntityId(),
                        options,
                        location,
                        entityPool,
                        entityType,
                        Villager.Profession.FARMER);
                case PLAYER:
                    if (npcOptions == null)
                        throw new IllegalArgumentException("NPCOptions must be set for NPC entity type");
                    return (T) new NPC(FakeEntityService.getService.getFreeEntityId(),
                            options,
                            location,
                            entityPool,
                            npcOptions);
            default:
                return (T) new LivingFakeEntity(FakeEntityService.getService.getFreeEntityId(),
                        options,
                        location,
                        entityPool,
                        entityType);
        }
    }
}
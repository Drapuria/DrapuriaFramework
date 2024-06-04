package net.drapuria.framework.bukkit.fake.entity.living;

import lombok.Getter;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityOptions;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityPool;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

@Getter
public class VillagerFakeEntity extends LivingFakeEntity {

    private Villager.Profession profession;

    public VillagerFakeEntity(int entityId, FakeEntityOptions options, Location location,
                              FakeEntityPool entityPool, EntityType entityType, Villager.Profession profession) {
        super(entityId, options, location, entityPool, entityType);
        this.profession = profession;
        super.dataWatcher.setObject(16, profession.ordinal());
    }

    public void setProfession(Villager.Profession profession) {
        this.profession = profession;
        super.dataWatcher.setObject(16, profession.ordinal());
    }
}

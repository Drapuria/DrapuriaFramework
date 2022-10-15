package net.drapuria.framework.bukkit.fake.entity.living;

import lombok.Getter;
import net.drapuria.framework.bukkit.fake.entity.FakeEntity;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityOptions;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@Getter
public class LivingFakeEntity extends FakeEntity {

    private EntityType entityType;

    public LivingFakeEntity(int entityId, FakeEntityOptions options) {
        super(entityId, options);
    }

    @Override
    public void show(Player player) {

    }

    @Override
    public void hide(Player player) {

    }

    @Override
    public void tickActionForPlayer(Player player) {

    }
}

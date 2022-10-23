package net.drapuria.framework.bukkit.fake.entity.living;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
import net.drapuria.framework.bukkit.fake.entity.FakeEntity;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityOptions;
import net.drapuria.framework.bukkit.fake.entity.helper.DataWatchHelper;
import net.drapuria.framework.bukkit.fake.entity.living.modifier.LivingFakeEntityVisibilityModifier;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@Getter
public class LivingFakeEntity extends FakeEntity {

    private EntityType entityType;
    private final WrappedDataWatcher dataWatcher;

    public LivingFakeEntity(int entityId, FakeEntityOptions options, final Location location, final EntityType entityType) {
        super(entityId, location, options);
        this.entityType = entityType;
        this.dataWatcher = DataWatchHelper.createDefaultWatcher(this);
    }

    @Override
    public void show(Player player) {
        player.sendMessage("SHOWING?");
        super.seeingPlayers.add(player);
        new LivingFakeEntityVisibilityModifier(this)
                .queueSpawn()
                .send(player);
    }

    @Override
    public void hide(Player player) {
        new LivingFakeEntityVisibilityModifier(this)
                .queueDestroy()
                .send(player);
        super.seeingPlayers.remove(player);
    }

    @Override
    public void tickActionForPlayer(Player player) {

    }
}

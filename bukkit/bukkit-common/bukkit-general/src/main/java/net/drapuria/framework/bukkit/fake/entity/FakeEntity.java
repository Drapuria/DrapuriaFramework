package net.drapuria.framework.bukkit.fake.entity;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.fake.hologram.FakeEntityHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public abstract class FakeEntity {

    protected final Collection<Player> seeingPlayers = new CopyOnWriteArrayList<>();
    protected final Collection<Player> includedOrExcludedPlayers = new CopyOnWriteArrayList<>();

    private Location location;
    private final int entityId;
    protected final FakeEntityPool entityPool;
    protected final FakeEntityOptions options;
    protected FakeEntityHologram hologram;

    private boolean isRespawning;

    protected FakeEntity(int entityId, Location location, FakeEntityPool entityPool, FakeEntityOptions options) {
        this.entityId = entityId;
        this.entityPool = entityPool;
        this.options = options;
        this.location = location;
    }

    public boolean isShownTo(final Player player) {
        return this.seeingPlayers.contains(player);
    }

    public void tickAnimation() {
        // TODO
    }

    public abstract void show(final Player player);

    public abstract void hide(final Player player);

    public abstract void tickActionForPlayer(final Player player);

    public abstract void respawn();

}

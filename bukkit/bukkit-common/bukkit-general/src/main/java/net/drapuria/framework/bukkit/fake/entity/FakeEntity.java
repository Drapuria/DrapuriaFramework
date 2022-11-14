package net.drapuria.framework.bukkit.fake.entity;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.fake.entity.living.LivingFakeEntity;
import net.drapuria.framework.bukkit.fake.entity.npc.NPC;
import net.drapuria.framework.bukkit.fake.hologram.FakeEntityHologram;
import net.drapuria.framework.bukkit.fake.hologram.line.ItemLine;
import net.drapuria.framework.bukkit.fake.hologram.line.Line;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public abstract class FakeEntity {

    protected transient final Collection<Player> seeingPlayers = new CopyOnWriteArrayList<>();
    protected transient final Collection<Player> includedOrExcludedPlayers = new CopyOnWriteArrayList<>();

    protected Location location;
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

    public void setLocation(Location location) {
        respawn();
        this.location = location;
        if (this.hologram != null)
            this.hologram.updateLocation();
    }

    public abstract void show(final Player player);

    public abstract void hide(final Player player);

    public abstract void tickActionForPlayer(final Player player);

    public abstract void respawn();

    protected double getHologramY(final List<Line> lines) {
        double i = getHologramHeight();
        for (Line line : lines) {
            if (line instanceof ItemLine) {
                i -= 0.44;
            }
        }
        return i;
    }

    public double getHologramHeight() {
        if (this instanceof NPC) {
            return 2;
        }
        switch (((LivingFakeEntity) this).getEntityType()) {
            case VILLAGER:
                return 2.15;
            case WITCH:
                return 2.7;
            case ENDERMAN:
                return 3;
            case ENDER_CRYSTAL:
                return 2.25;
            case ZOMBIE:
            case PIG_ZOMBIE:
            case SKELETON:
            case BLAZE:
            case SLIME:
            case MAGMA_CUBE:
                return 2.2;
            case SPIDER:
            case CAVE_SPIDER:
            case PIG:
                return 1.5;
            case CREEPER:
                return 1.8;
            case COW:
            case MUSHROOM_COW:
                return 1.85;
            case IRON_GOLEM:
                return 2.85;
            case GUARDIAN:
                return 1;
            default:
                return 2;
        }
    }

    public static float getPerfectDirection(double rot) {
        rot = (rot - 90.0F) % 360.0F;
        if (rot < 0.0F) {
            rot += 360.0F;
        }
        if (0.0 <= rot && rot < 22.5) {
            return getMedian(0, 22.5F);
        }
        if (22.5 <= rot && rot < 67.5) {
            return getMedian(22.5F, 67.5F);
        }
        if (67.5 <= rot && rot < 112.5) {
            return getMedian(67.5F, 112.5F);
        }
        if (112.5 <= rot && rot < 157.5) {
            return getMedian(112.5F, 157.5F);
        }
        if (157.5 <= rot && rot < 202.5) {
            return getMedian(157.5F, 202.5F);

        }
        if (202.5 <= rot && rot < 247.5) {
            return getMedian(202.5F, 247.5F);
        }
        if (247.5 <= rot && rot < 292.5) {
            return getMedian(247.5F, 292.5F);
        }
        if (292.5 <= rot && rot < 337.5) {
            return getMedian(292.5F, 337.5F);
        }
        if (337.5 <= rot && rot < 360.0) {
            return getMedian(337.5F, 360.0F);
        }
        return getMedian(0, 22.5F);
    }

    private static float getMedian(float f1, float f2) {
        String s = String.valueOf((f1 + f2) / 2.0F);
        if (s.contains(".")) {
            return Float.parseFloat(s.split("[.]")[0]);
        } else {
            return Float.parseFloat(s);
        }
    }
}

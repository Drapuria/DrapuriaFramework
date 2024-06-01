package net.drapuria.framework.bukkit.fake.hologram;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.fake.FakeShowType;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramHelper;
import net.drapuria.framework.bukkit.fake.hologram.helper.PacketHelper;
import net.drapuria.framework.bukkit.fake.hologram.line.ConsumedTextLine;
import net.drapuria.framework.bukkit.fake.hologram.line.Line;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerHologram implements Hologram {

    private static final HologramService hologramService = DrapuriaCommon.getBean(HologramService.class);

    @Getter
    private final Player player;
    private final List<Line> lines = new ArrayList<>();

    private Location location;
    private boolean loaded = false;

    private boolean isBoundToPlayer = false;
    private Player boundTo = null;
    private double boundYOffset = 0.6;

    public PlayerHologram(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void show(Player player) {
        if (!this.loaded && player.getWorld().equals(this.location.getWorld())) {
            this.loaded = true;
            this.sendLines();
        }
    }

    @Override
    public void hide(Player player) {
        if (this.loaded && this.player.getWorld().equals(this.location.getWorld())) {
            for (Line line : this.lines) PacketHelper.sendPackets(this.player, line.getDestroyPackets());
        }
        this.loaded = false;
    }

    @Override
    public void setLocation(Location location) {
        final Location oldLoc = this.location;
        this.location = location;
        if (!HologramHelper.isInRange(this.player.getLocation(), this.location)) {
            hide(this.player);
            return;
        }
        double oldCurrentY = oldLoc.getY();
        double currentY = this.location.getY();
        for (int i = 0; i < this.lines.size(); i++) {
            final Line line = this.lines.get(i);
            if (i != 0) {
                currentY -= line.getHeight();
                currentY -= 0.04D;
                oldCurrentY -= line.getHeight();
                oldCurrentY -= 0.04D;
            }
            PacketHelper.sendPackets(player, line.getTeleportPackets(player, oldLoc.getX(), oldCurrentY, oldLoc.getZ(), this.getLocation().getX(), currentY, this.getLocation().getZ()));
        }
        this.checkHologram();
    }

    public void addLine(Line line) {
        this.lines.add(line);
       // this.updateLine(line);
        this.refreshLines();
    }

    public void removeLine(Line line) {
        this.lines.remove(line);
        PacketHelper.sendPackets(this.player, line.getDestroyPackets());
       // this.updateLine(line);
    }

    public void updateLines() {
        for (Line line : lines) {
            this.updateLine(line);
        }
    }

    @Override
    public void updateConsumedLines() {
        for (Line line : this.lines) {
            if (line instanceof ConsumedTextLine) {
             this.updateLine(line);
            }
        }
    }

    public void refreshLines() {
        for (Line line : this.lines) {
            PacketHelper.sendPackets(this.player, line.getDestroyPackets());
        }
        this.sendLines();
    }

    private void sendLines() {
        double currentY = this.location.getY();
        for (int i = 0; i < this.lines.size(); i++) {
            final Line line = this.lines.get(i);
            if (i != 0) {
                currentY -= line.getHeight();
                currentY -= 0.04D;
            }
            PacketHelper.sendPackets(player, line.getSpawnPackets(player, this.getLocation().getX(), currentY, this.getLocation().getZ()));
        }
    }

    public void updateLine(Line line) {
        PacketHelper.sendPackets(this.player, line.getUpdatePackets(this.player));
    }

    @Override
    public void checkHologram() {
        final boolean inRange = HologramHelper.isInRange(this.player.getLocation(), this.location);
        if (!inRange && this.loaded)
            hide(this.player);
        else
            show(this.player);
    }

    @Override
    public void setLocationBoundToPlayer(boolean boundToPlayer) {
        this.isBoundToPlayer = boundToPlayer;
    }

    @Override
    public void setLocationBoundToPlayer(boolean boundToPlayer, Player player) {
        this.isBoundToPlayer = boundToPlayer;
        this.boundTo = player;
    }

    @Override
    public boolean isLocationBoundToPlayer() {
        return this.isBoundToPlayer;
    }

    @Override
    public Player getBoundPlayer() {
        return this.boundTo;
    }

    @Override
    public void setBoundPlayer(Player player) {
        this.boundTo = player;
    }

    @Override
    public void setBoundYOffset(double yOffset) {
        this.boundYOffset = yOffset;
    }

    @Override
    public double getBoundYOffset() {
        return this.boundYOffset;
    }

    @Override
    public FakeShowType getType() {
        return FakeShowType.PLAYER_BASED;
    }

    @Override
    public void setType(FakeShowType fakeShowType) {
        try {
            throw new NoSuchMethodException("You cannot set the FakeShowType of a PlayerHologram!");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Player> getIncludedOrExcludedPlayers() {
        throw new UnsupportedOperationException("Not excluding / including players in a PlayerHologram!");
    }

    @Override
    public void addExcludedOrIncludedPlayer(Player player) {

    }

    @Override
    public void removeExcludedOrIncludedPlayer(Player player) {

    }

    @Override
    public boolean isExcludedOrIncluded(Player player) {
        return false;
    }
}
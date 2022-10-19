package net.drapuria.framework.bukkit.fake.hologram;

import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.ImmutableList;
import lombok.NoArgsConstructor;
import net.drapuria.framework.bukkit.fake.FakeShowType;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramHelper;
import net.drapuria.framework.bukkit.fake.hologram.helper.PacketHelper;
import net.drapuria.framework.bukkit.fake.hologram.line.Line;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.bukkit.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@NoArgsConstructor
public class GlobalHologram implements Hologram {

    private final List<Line> lines = new ArrayList<>();
    private Location location;
    private transient final Set<Player> shownFor = new HashSet<>();
    private boolean isBoundToPlayer = false;
    private Player boundTo = null;
    private double boundYOffset = 0.8;
    private FakeShowType fakeShowType;
    private final List<Player> includedOrExcludedPlayers = new CopyOnWriteArrayList<>();

    @Override
    public Location getLocation() {
        return this.location;
    }

    public GlobalHologram(final Location location) {
        this.location = location;
    }

    @Override
    public void show(Player player) {
        if (player.getWorld().equals(this.location.getWorld()) && shownFor.add(player)) {
            double currentY = this.location.getY() + getFullHologramHeight();
            for (int i = 0; i < this.lines.size(); i++) {
                final Line line = this.lines.get(i);
                if (i != 0) {
                    currentY -= line.getHeight();
                    currentY -= 0.05D;
                }
                PacketHelper.sendPackets(player, line.getSpawnPackets(player, this.location.getX(), currentY, this.location.getZ()));
            }
        }
    }

    @Override
    public void hide(Player player) {
        if (shownFor.remove(player) && player.getWorld().equals(this.location.getWorld())) {
            for (Line line : this.lines) {
                PacketHelper.sendPackets(player, line.getDestroyPackets());
            }
        }
    }

    public void broadcastPackets(final PacketContainer[] packets) {
        for (Player player : this.shownFor)
            PacketHelper.sendPackets(player, packets);
    }

    @Override
    public void setLocation(Location location) {
        final Location oldLocation = this.location;
        this.location = location;
        for (Player player : shownFor) {
            if (!HologramHelper.isInRange(player.getLocation(), this.location)) hide(player);
        }

        double oldCurrentY = oldLocation.getY() + getFullHologramHeight();
        double currentY = this.location.getY() + getFullHologramHeight();
        for (int i = 0; i < this.lines.size(); i++) {
            Line line = this.lines.get(i);
            for (Player player : this.shownFor) {
                PacketHelper.sendPackets(player, line.getTeleportPackets(player, oldLocation.getX(), oldCurrentY, oldLocation.getZ(), this.location.getX(), currentY, this.location.getZ()));
            }
            currentY -= line.getHeight();
            currentY -= 0.05D;
            oldCurrentY -= line.getHeight();
            oldCurrentY -= 0.05D;
        }
        checkHologram();
    }

    public void addLine(final Line line) {
        this.lines.add(line);
    }

    public void removeLine(final Line line) {
        this.lines.remove(line);
        broadcastPackets(line.getDestroyPackets());
    }

    public void addLine(final int index, final Line line) {
        this.lines.add(index, line);
    }

    public void removeLine(final int index) {
        final Line line = this.lines.remove(index);
        if (line != null)
            broadcastPackets(line.getDestroyPackets());
    }

    public void updateLine(final Line line) {
        for (Player player : shownFor) {
            PacketHelper.sendPackets(player, line.getUpdatePackets(player));
        }
    }

    public void updateLines() {
        for (Line line : lines) {
            updateLine(line);
        }
    }

    void destroy() {
        shownFor.forEach(this::hide);
    }

    public void refreshLines() {
        for (Line line : lines) {
            PacketContainer[] destroyPacket = line.getDestroyPackets();
            for (Player player : shownFor) {
                PacketHelper.sendPackets(player, destroyPacket);
            }
        }
        double currentY = this.location.getY() + getFullHologramHeight();
        for (int i = 0; i < this.lines.size(); i++) {
            final Line line = this.lines.get(i);

            for (Player player : this.shownFor) {
                PacketHelper.sendPackets(player, line.getSpawnPackets(player, this.location.getX(), currentY, this.location.getZ()));
            }
            currentY -= line.getHeight();
            currentY -= 0.05D;
        }
    }

    public boolean isLoaded(final Player player) {
        return this.shownFor.contains(player);
    }

    @Override
    public void checkHologram() {
        for (final Player player : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
            checkHologram(player);
        }
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
        return this.fakeShowType;
    }

    @Override
    public void setType(FakeShowType fakeShowType) {
        if (fakeShowType == FakeShowType.PLAYER_BASED) {
            throw new UnsupportedOperationException("Cannot set GlobalHologram FakeShowType to PLAYER_BASED!");
        }
        this.fakeShowType = fakeShowType;
    }

    @Override
    public List<Player> getIncludedOrExcludedPlayers() {
        return this.includedOrExcludedPlayers;
    }

    @Override
    public void addExcludedOrIncludedPlayer(Player player) {
        if (player instanceof DrapuriaPlayer)
            player = player.getPlayer();
        this.includedOrExcludedPlayers.add(player);
        if (this.isLoaded(player) && this.fakeShowType == FakeShowType.EXCLUDING)
            hide(player);
    }

    @Override
    public void removeExcludedOrIncludedPlayer(Player player) {
        if (player instanceof DrapuriaPlayer)
            player = player.getPlayer();
        this.includedOrExcludedPlayers.remove(player);
        if (this.isLoaded(player) && this.fakeShowType == FakeShowType.INCLUDING)
            hide(player);
    }

    @Override
    public boolean isExcludedOrIncluded(Player player) {
        return this.includedOrExcludedPlayers.contains(player);
    }

    public void checkHologram(final Player player) {
        if (this.fakeShowType != FakeShowType.ALL
                && ((this.fakeShowType == FakeShowType.INCLUDING && !this.isExcludedOrIncluded(player)) ||
                (this.fakeShowType == FakeShowType.EXCLUDING && this.isExcludedOrIncluded(player)))) return;

        final boolean isInRange = HologramHelper.isInRange(player.getLocation(), this.location);
        if (!isInRange && this.isLoaded(player)) {
            hide(player);
            return;
        }
        if (isInRange && !this.isLoaded(player))
            show(player);
    }

    private double getFullHologramHeight() {
        double height = 0.0D;
        for (int i = 0; i < this.lines.size(); i++) {
            Line line = this.lines.get(i);
            if (i + 1 < this.lines.size()) {
                height += 0.05D;
                height += line.getHeight();
            }
        }
        return height;
    }
}

package net.drapuria.framework.bukkit.fake.hologram;

import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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

public class GlobalHologram implements Hologram {

    private final List<Line> lines = new ArrayList<>();
    private final List<Line> clientSynchronized = new ArrayList<>();
    private Location location;
    private transient final Set<Player> shownFor = new HashSet<>();
    private boolean isBoundToPlayer = false;
    private Player boundTo = null;
    private double boundYOffset = 0.8;
    private FakeShowType fakeShowType = FakeShowType.ALL;
    private final List<Player> includedOrExcludedPlayers = new CopyOnWriteArrayList<>();
    private final HologramAlignment alignment;

    @Override
    public Location getLocation() {
        return this.location;
    }

    public GlobalHologram(final Location location) {
        this(location, HologramAlignment.STACK_UP);
    }

    public GlobalHologram(final Location location, final HologramAlignment alignment) {
        this.location = location;
        this.alignment = alignment;
    }

    public GlobalHologram() {
        this.alignment = HologramAlignment.STACK_UP;
    }

    @Override
    public void show(Player player) {
        if (player.getWorld().equals(this.location.getWorld()) && shownFor.add(player)) {
            double currentY = this.location.getY() + getFullHologramHeight() + getAlignmentAdjustmentHeight();
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

        double oldCurrentY = oldLocation.getY() + getFullHologramHeight() + getAlignmentAdjustmentHeight();
        double currentY = this.location.getY() + getFullHologramHeight() + getAlignmentAdjustmentHeight();
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
        synchronizeLines();
    }

    public void removeLine(final Line line) {
        this.lines.remove(line);
        broadcastPackets(line.getDestroyPackets());
        synchronizeLines();
    }

    public void removeLines() {
        for (final Line line : ImmutableList.copyOf(this.lines)) {
            this.lines.remove(line);
            this.broadcastPackets(line.getDestroyPackets());
        }
    }

    private double getAlignmentAdjustmentHeight() {
        final double height = this.getFullHologramHeight();
        if (this.alignment == HologramAlignment.STACK_UP) {
            return 0.0;
        }
        if (this.alignment == HologramAlignment.STACK_DOWN) {
            return -height;
        }
        return -height / 2.0;
    }

    public void synchronizeLines() {
        double currentY = this.location.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
        for (int lineIndex = 0; lineIndex < this.lines.size(); ++lineIndex) {
            final Line line2 = this.lines.get(lineIndex);
            currentY -= line2.getHeight();
            currentY -= 0.05;
            final int syncedBefore = this.clientSynchronized.indexOf(line2);
            if (syncedBefore == -1) {
                for (final Player loaded : this.shownFor) {
                    PacketHelper.sendPackets(loaded, line2.getSpawnPackets(loaded, this.location.getX(), currentY, this.location.getZ()));
                }
            } else if (syncedBefore != lineIndex) {
                for (final Player loaded : this.shownFor) {
                    final PacketContainer[] packets = line2.getTeleportPackets(loaded, 0.0, 0.0, 0.0, this.location.getX(), currentY, this.location.getZ());
                    PacketHelper.sendPackets(loaded, packets);
                }
            }
        }
        this.clientSynchronized.stream().filter(l -> !this.lines.contains(l)).forEach(line -> {
            PacketContainer[] despawnPackets = line.getDestroyPackets();
            for (Player player : this.shownFor) {
                PacketHelper.sendPackets(player, despawnPackets);
            }
        });
        this.clientSynchronized.clear();
        this.clientSynchronized.addAll(this.lines);
    }

    public void addLine(final int index, final Line line) {
        this.lines.add(index, line);
    }

    public void removeLine(final int index) {
        final Line line = this.lines.remove(index);
        if (line != null) {
            broadcastPackets(line.getDestroyPackets());
            synchronizeLines();
        }
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

    public void destroy() {
        ImmutableSet.copyOf(this.shownFor).forEach(this::hide);
    }

    public void refreshLines() {
        for (Line line : lines) {
            PacketContainer[] destroyPacket = line.getDestroyPackets();
            for (Player player : shownFor) {
                PacketHelper.sendPackets(player, destroyPacket);
            }
        }
        double currentY = this.location.getY() + getFullHologramHeight() + getAlignmentAdjustmentHeight();
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
                (this.fakeShowType == FakeShowType.EXCLUDING && this.isExcludedOrIncluded(player)))) {
            return;
        }

        final boolean isInRange = HologramHelper.isInRange(player.getLocation(), this.location);
        if (!isInRange && this.isLoaded(player)) {
            hide(player);
            return;
        }
        if (isInRange && !this.isLoaded(player)) {
            show(player);
        }
    }

    public List<Line> getLines() {
        return lines;
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

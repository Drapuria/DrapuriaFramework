package net.drapuria.framework.bukkit.fake.hologram;

import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.ImmutableList;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramHelper;
import net.drapuria.framework.bukkit.fake.hologram.helper.PacketHelper;
import net.drapuria.framework.bukkit.fake.hologram.line.Line;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalHologram implements Hologram {

    private final List<Line> lines = new ArrayList<>();
    private Location location;
    private final Set<Player> shownFor = new HashSet<>();

    @Override
    public Location getLocation() {
        return this.location;
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
            if (i != 0) {
                currentY -= line.getHeight();
                currentY -= 0.05D;
                oldCurrentY -= line.getHeight();
                oldCurrentY -= 0.05D;
            }
            for (Player player : this.shownFor) {
                PacketHelper.sendPackets(player, line.getTeleportPackets(player, oldLocation.getX(), oldCurrentY, oldLocation.getZ(), this.location.getX(), currentY, this.location.getZ()));
            }
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
            if (i != 0) {
                currentY -= line.getHeight();
                currentY -= 0.05D;
            }
            for (Player player : this.shownFor) {
                PacketHelper.sendPackets(player, line.getSpawnPackets(player, this.location.getX(), currentY, this.location.getZ()));
            }
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

    public void checkHologram(final Player player) {
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

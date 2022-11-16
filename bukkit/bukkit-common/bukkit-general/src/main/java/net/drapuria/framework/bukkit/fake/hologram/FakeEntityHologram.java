package net.drapuria.framework.bukkit.fake.hologram;

import lombok.NoArgsConstructor;
import net.drapuria.framework.bukkit.fake.FakeShowType;
import net.drapuria.framework.bukkit.fake.entity.FakeEntity;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramHelper;
import net.drapuria.framework.bukkit.fake.hologram.helper.PacketHelper;
import net.drapuria.framework.bukkit.fake.hologram.line.Line;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class FakeEntityHologram implements Hologram {

    private transient FakeEntity fakeEntity;
    @SuppressWarnings("FieldMayBeFinal")
    private List<Line> lines = new ArrayList<>();
    private transient final Map<Player, List<Line>> playerLines = new HashMap<>(); // ??
    private transient final Map<Player, Location> playerDefinedLocations = new HashMap<>(); // to handle sneak etc
    private transient Location location;

    public FakeEntityHologram(FakeEntity fakeEntity) {
        this.fakeEntity = fakeEntity;
        this.location = this.fakeEntity.getLocation().clone();
        this.location.setY(this.location.getY() + fakeEntity.getHologramY(this.lines));
    }

    public Location getLocation(final Player player) {
        return this.playerDefinedLocations.getOrDefault(player, this.getLocation());
    }

    public void updateLocation() {
        this.location = this.fakeEntity.getLocation().clone();
        this.location.setY(this.location.getY() + fakeEntity.getHologramY(this.lines));
        this.fakeEntity.getSeeingPlayers().forEach(player -> teleportTo(player, this.location));
    }

    public void setFakeEntity(FakeEntity fakeEntity) {
        this.fakeEntity = fakeEntity;
        this.location = this.fakeEntity.getLocation().clone();
        this.location.setY(this.location.getY() + fakeEntity.getHologramY(this.lines));
    }

    public void setPlayerLocation(final Player player, Location location) {
        if (location == null) {
            teleportTo(player, this.location);
            this.playerDefinedLocations.remove(player);
            return;
        }
        teleportTo(player, location);
        this.playerDefinedLocations.put(player, location);
    }

    private void teleportTo(final Player player, final Location location) {
        Location oldLocation = this.getLocation(player);
        double oldCurrentY = oldLocation.getY() + getFullHologramHeight();
        double currentY = location.getY() + getFullHologramHeight();
        for (int i = 0; i < this.lines.size(); i++) {
            Line line = this.lines.get(i);
            if (i != 0) {
                currentY -= line.getHeight();
                currentY -= 0.05D;
                oldCurrentY -= line.getHeight();
                oldCurrentY -= 0.05D;
            }
            PacketHelper.sendPackets(player, line.getTeleportPackets(player, oldLocation.getX(), oldCurrentY, oldLocation.getZ(), location.getX(), currentY, location.getZ()));

        }
    }

    @Override
    public Location getLocation() {
        return this.location;
    }


    @Override
    public void show(Player player) {
        double currentY = this.getLocation(player).getY() + getFullHologramHeight();
        for (int i = 0; i < this.lines.size(); i++) {
            final Line line = this.lines.get(i);
            if (i != 0) {
                currentY -= line.getHeight();
                currentY -= 0.05D;
            }
            PacketHelper.sendPackets(player, line.getSpawnPackets(player, this.getLocation(player).getX(), currentY, this.getLocation(player).getZ()));
        }
    }

    @Override
    public void hide(Player player) {
        if (player.getWorld().equals(this.location.getWorld())) {
            for (final Line line : this.lines) {
                PacketHelper.sendPackets(player, line.getDestroyPackets());
            }
        }
    }

    @Override
    public void setLocation(Location location) {
        location.setY(location.getY() + fakeEntity.getHologramY(lines));
        final Location oldLocation = this.location.clone();
        this.location = location;
        for (Player player : fakeEntity.getSeeingPlayers()) {
            if (!HologramHelper.isInRange(player.getLocation(), this.getLocation(player))) hide(player);
        }

        double oldCurrentY = oldLocation.getY() + getFullHologramHeight();
        double currentY = this.getLocation().getY() + getFullHologramHeight();
        for (int i = 0; i < this.lines.size(); i++) {
            Line line = this.lines.get(i);
            for (Player player : this.fakeEntity.getSeeingPlayers()) {
                PacketHelper.sendPackets(player, line.getTeleportPackets(player, oldLocation.getX(), oldCurrentY, oldLocation.getZ(), this.getLocation().getX(), currentY, this.getLocation().getZ()));
            }
            currentY -= line.getHeight();
            currentY -= 0.05D;
            oldCurrentY -= line.getHeight();
            oldCurrentY -= 0.05D;
        }
        checkHologram();
    }

    @Override
    public void checkHologram() {

    }

    public void addLine(final Line line) {
        this.fakeEntity.getSeeingPlayers().forEach(this::hide);
        this.lines.add(line);
        this.fakeEntity.getSeeingPlayers().forEach(this::show);
    }

    public void removeLine(final Line line) {
        this.fakeEntity.getSeeingPlayers().forEach(this::hide);
        this.lines.remove(line);
        this.fakeEntity.getSeeingPlayers().forEach(this::show);
    }

    public void addLine(final int index, final Line line) {
        this.fakeEntity.getSeeingPlayers().forEach(this::hide);
        this.lines.add(index, line);
        this.fakeEntity.getSeeingPlayers().forEach(this::show);
    }

    public void removeLine(final int index) {
        this.fakeEntity.getSeeingPlayers().forEach(this::hide);
        this.lines.remove(index);
        this.fakeEntity.getSeeingPlayers().forEach(this::show);
    }

    public void removePlayerLines(final Player player) {
        this.fakeEntity.getSeeingPlayers().forEach(this::hide);
        this.playerLines.remove(player);
        this.fakeEntity.getSeeingPlayers().forEach(this::show);
    }

    public List<Line> getLines() {
        return lines;
    }

    public Map<Player, List<Line>> getPlayerLines() {
        return playerLines;
    }

    @Override
    public void setLocationBoundToPlayer(boolean boundToPlayer) {
        throw new UnsupportedOperationException("Cannot bind FakeEntityHologram to player!");
    }

    @Override
    public void setLocationBoundToPlayer(boolean boundToPlayer, Player player) {
        throw new UnsupportedOperationException("Cannot bind FakeEntityHologram to player!");
    }

    @Override
    public boolean isLocationBoundToPlayer() {
        throw new UnsupportedOperationException("Cannot bind FakeEntityHologram to player!");
    }

    @Override
    public Player getBoundPlayer() {
        throw new UnsupportedOperationException("Cannot bind FakeEntityHologram to player!");
    }

    @Override
    public void setBoundPlayer(Player player) {
        throw new UnsupportedOperationException("Cannot bind FakeEntityHologram to player!");
    }

    @Override
    public void setBoundYOffset(double yOffset) {
        throw new UnsupportedOperationException("Cannot bind FakeEntityHologram to player!");
    }

    @Override
    public double getBoundYOffset() {
        throw new UnsupportedOperationException("Cannot bind FakeEntityHologram to player!");
    }

    @Override
    public FakeShowType getType() {
        throw new UnsupportedOperationException("Show type is handled by the FakeEntity!");
    }

    @Override
    public void setType(FakeShowType fakeShowType) {
        throw new UnsupportedOperationException("Show type is handled by the FakeEntity!");
    }

    @Override
    public List<Player> getIncludedOrExcludedPlayers() {
        throw new UnsupportedOperationException("Included or excluded players are handled by the FakeEntity!");

    }

    @Override
    public void addExcludedOrIncludedPlayer(Player player) {
        throw new UnsupportedOperationException("Included or excluded players are handled by the FakeEntity!");

    }

    @Override
    public void removeExcludedOrIncludedPlayer(Player player) {
        throw new UnsupportedOperationException("Included or excluded players are handled by the FakeEntity!");

    }

    @Override
    public boolean isExcludedOrIncluded(Player player) {
        throw new UnsupportedOperationException("Included or excluded players are handled by the FakeEntity!");
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
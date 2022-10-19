package net.drapuria.framework.bukkit.fake.hologram;

import com.google.common.collect.ImmutableSet;
import lombok.Setter;
import net.drapuria.framework.bukkit.fake.FakeShowType;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramHelper;
import net.drapuria.framework.bukkit.fake.hologram.helper.PacketHelper;
import net.drapuria.framework.bukkit.fake.hologram.line.Line;
import net.drapuria.framework.bukkit.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerDefinedHologram implements Hologram {

    private Location location;
    private final Set<Player> shownFor = new HashSet<>();
    private final Map<Player, List<Line>> playerLines = new HashMap<>();
    @Setter
    private IPlayerDefinedHologram interfaze = null;
    private boolean isBoundToPlayer = false;
    private Player boundTo = null;
    private double boundYOffset = 0.6;
    private FakeShowType fakeShowType;
    private final List<Player> includedOrExcludedPlayers = new CopyOnWriteArrayList<>();

    public PlayerDefinedHologram(final Location location, final IPlayerDefinedHologram interfaze) {
        this(location);
        this.interfaze = interfaze;
    }

    public PlayerDefinedHologram(final Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void show(Player player) {
        if (!player.getWorld().equals(this.location.getWorld()) && !this.shownFor.add(player)) return;
        final List<Line> lines = this.playerLines.getOrDefault(player, onLoad(player));
        changeLines(player, lines);

    }

    @Override
    public void hide(Player player) {
        if (!this.shownFor.remove(player) || !player.getWorld().equals(this.location.getWorld())) return;
        onUnload(player);
        removeLines(player);
    }

    private List<Line> onLoad(final Player player) {
        if (this.interfaze != null)
            return this.interfaze.getLines(player);
        return Collections.emptyList();
    }

    private void onUnload(final Player player) {
        if (this.interfaze != null)
            this.interfaze.onUnload(player);
    }

    private void changeLines(final Player player, List<Line> newLines) {
        if (newLines == null || newLines.isEmpty()) return;
        if (this.playerLines.containsKey(player) && !this.playerLines.get(player).isEmpty())
            removeLines(player);
        this.playerLines.put(player, newLines);
        double currentY = this.location.getY();
        for (int i = 0; i < newLines.size(); i++) {
            final Line line = newLines.get(i);
            if (i != 0) {
                currentY -= line.getHeight();
            }
            PacketHelper.sendPackets(player, line.getSpawnPackets(player, this.location.getX(), currentY, this.location.getZ()));
        }
    }

    public void removeLines(final Player player) {
        if (!this.playerLines.containsKey(player)) return;
        for (final Line line : this.playerLines.get(player)) {
            PacketHelper.sendPackets(player, line.getDestroyPackets());
        }
        this.playerLines.remove(player);
    }

    private double getFullHologramHeight(List<Line> lines) {
        double height = 0.0D;
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            if (i + 1 < lines.size()) {
                height += 0.05D;
                height += line.getHeight();
            }
        }
        return height;
    }

    public void destroy() {
        ImmutableSet.copyOf(this.shownFor).forEach(this::hide);
    }

    @Override
    public void setLocation(Location location) {
        final Location oldLocation = this.location;
        this.location = location;
        for (Player player : this.shownFor) {
            if (!HologramHelper.isInRange(player.getLocation(), this.location))
                hide(player);
        }
        for (Map.Entry<Player, List<Line>> entrySet : this.playerLines.entrySet()) {
            final Player player = entrySet.getKey();
            final List<Line> lines = entrySet.getValue();
            double oldCurrentY = oldLocation.getY();
            double currentY = this.location.getY();
            for (int i = 0; i < lines.size(); i++) {
                final Line line = lines.get(i);
                if (i != 0) {
                    currentY -= line.getHeight();
                    currentY -= 0.02D;
                    oldCurrentY -= line.getHeight();
                    oldCurrentY -= 0.02D;
                }
                PacketHelper.sendPackets(player,
                        line.getTeleportPackets(
                                player, oldLocation.getX(), oldCurrentY, oldLocation.getZ(),
                                location.getX(), currentY, location.getZ())
                );
            }
        }
        checkHologram();
    }

    public boolean isLoaded(final Player player) {
        return this.shownFor.contains(player);
    }

    @Override
    public void checkHologram() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            this.checkHologram(player);
        }
    }

    public void checkHologram(final Player player) {
        if (this.fakeShowType != FakeShowType.ALL
                && ((this.fakeShowType == FakeShowType.INCLUDING && !this.isExcludedOrIncluded(player)) ||
                (this.fakeShowType == FakeShowType.EXCLUDING && this.isExcludedOrIncluded(player)))) return;
        final boolean isInRange = HologramHelper.isInRange(player.getLocation(), this.location);
        if (!isInRange && isLoaded(player)) {
            hide(player);
            return;
        }
        if (isInRange && !isLoaded(player))
            show(player);
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
        this.includedOrExcludedPlayers.add(player);
    }

    @Override
    public void removeExcludedOrIncludedPlayer(Player player) {
        this.includedOrExcludedPlayers.remove(player);
    }

    @Override
    public boolean isExcludedOrIncluded(Player player) {
        return this.includedOrExcludedPlayers.contains(player);
    }


    public interface IPlayerDefinedHologram {

        List<Line> getLines(final Player player);

        void onUnload(final Player player);

    }

}

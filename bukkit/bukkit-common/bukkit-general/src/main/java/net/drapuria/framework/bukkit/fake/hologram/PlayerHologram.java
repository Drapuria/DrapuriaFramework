package net.drapuria.framework.bukkit.fake.hologram;

import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.fake.hologram.helper.HologramHelper;
import net.drapuria.framework.bukkit.fake.hologram.helper.PacketHelper;
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

    @Override
    public void checkHologram() {
        final boolean inRange = HologramHelper.isInRange(this.player.getLocation(), this.location);
        if (!inRange && this.loaded)
            hide(this.player);
        else
            show(this.player);
    }
}
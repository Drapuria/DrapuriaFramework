package net.drapuria.framework.bukkit.fake.hologram.repository;

import net.drapuria.framework.bukkit.fake.hologram.GlobalHologram;
import net.drapuria.framework.bukkit.fake.hologram.Hologram;
import net.drapuria.framework.bukkit.fake.hologram.PlayerDefinedHologram;
import org.bukkit.entity.Player;

import java.util.*;

public class HologramRepository {

    private final Map<Player, List<Hologram>> holograms = new HashMap<>();
    private final List<GlobalHologram> globalHolograms = new ArrayList<>();
    private final List<PlayerDefinedHologram> playerDefinedHolograms = new ArrayList<>();

    public List<Hologram> getHolograms(final Player player) {
        return this.holograms.getOrDefault(player, Collections.emptyList());
    }

    public void createPlayerHologramRepository(final Player player) {
        if (this.holograms.containsKey(player))
            return;
        this.holograms.put(player, new ArrayList<>());
    }

    public List<GlobalHologram> getGlobalHolograms() {
        return this.globalHolograms;
    }

    public List<PlayerDefinedHologram> getPlayerDefinedHolograms() {
        return this.playerDefinedHolograms;
    }
}


package net.drapuria.framework.bukkit.fake.hologram.repository;

import net.drapuria.framework.bukkit.fake.hologram.GlobalHologram;
import net.drapuria.framework.bukkit.fake.hologram.Hologram;
import net.drapuria.framework.bukkit.fake.hologram.PlayerDefinedHologram;
import net.drapuria.framework.bukkit.fake.hologram.PlayerHologram;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class HologramRepository {

    private final Map<Player, List<PlayerHologram>> holograms = new HashMap<>();
    private final List<GlobalHologram> globalHolograms = new ArrayList<>();
    private final List<PlayerDefinedHologram> playerDefinedHolograms = new ArrayList<>();

    public List<PlayerHologram> getHolograms(final Player player) {
        return this.holograms.getOrDefault(player, Collections.emptyList());
    }

    public List<PlayerHologram> getPlayerHolograms() {
        return this.holograms.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
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


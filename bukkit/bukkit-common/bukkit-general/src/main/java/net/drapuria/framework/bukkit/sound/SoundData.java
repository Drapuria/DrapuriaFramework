/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.sound;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Data
public class SoundData {

    private final Sound sound;
    private final float volume, pitch;

    public SoundData(Sound sound) {
        this(sound, 1F, 1F);
    }

    public SoundData(Sound sound, float volume) {
        this(sound, volume, 1F);
    }

    public SoundData(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(Player... players) {
        for (Player player : players) {
            player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
        }
    }

    public void play(Iterable<Player> players) {
        for (Player player : players) {
            player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
        }
    }

    public void play(Player player) {
        player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
    }

    public void play(Location location) {
        location.getWorld().playSound(location, this.sound, this.volume, this.pitch);
    }

    public void play(Location location, Player... players) {
        for (Player player : players)
            player.playSound(location, this.sound, this.volume, this.pitch);
    }

    public void play(Location location, Iterable<Player> players) {
        for (Player player : players) {
            player.playSound(location, this.sound, this.volume, this.pitch);
        }
    }

    public void play(World world) {
        world.getPlayers().forEach(this::play);
    }

    public static SoundData of(Sound sound) {
        return new SoundData(sound);
    }

    public static SoundData of(Sound sound, float volume) {
        return new SoundData(sound, volume);
    }

    public static SoundData of(Sound sound, float volume, float value) {
        return new SoundData(sound, volume, value);
    }
}
package net.drapuria.framework.bukkit.player;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.CompletableFuture;

@Getter
public class DrapuriaOfflinePlayer {

    private String name;
    private OfflinePlayer offlinePlayer;

    public DrapuriaOfflinePlayer(String name) {
        this.name = name;
    }

    public CompletableFuture<OfflinePlayer> getOfflinePlayer() {
        return offlinePlayer == null ? CompletableFuture
                .supplyAsync(() -> Bukkit.getOfflinePlayer(name))
                .thenApply(offlinePlayer1 -> this.offlinePlayer = offlinePlayer1)
                : CompletableFuture.completedFuture(offlinePlayer);
    }
}

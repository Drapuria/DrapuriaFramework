package net.drapuria.framework.bukkit.player;

import lombok.AccessLevel;
import lombok.Getter;
import net.drapuria.framework.FrameworkMisc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class DrapuriaOfflinePlayer  {

    private String name;
    private UUID uuid;

    @Getter(AccessLevel.NONE)
    private CompletableFuture<OfflinePlayer> future;

    public DrapuriaOfflinePlayer(String name) {
        this.name = name;
        this.future = CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(name));
        this.future.thenAccept(offlinePlayer -> {
            this.uuid = offlinePlayer.getUniqueId();
        });
    }

    public DrapuriaOfflinePlayer(UUID uuid) {
        this.future = CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(uuid));
        this.future.thenAccept(offlinePlayer -> {
            this.name = offlinePlayer.getName();
        });
    }

    public CompletableFuture<OfflinePlayer> getOfflinePlayer() {
        return future;
    }

    public void thenAcceptSync(Consumer<OfflinePlayer> consumer) {
        future.thenAccept(offlinePlayer -> FrameworkMisc.TASK_SCHEDULER.runSync(() -> consumer.accept(offlinePlayer)));
    }

    public void thenAccept(Consumer<OfflinePlayer> consumer) {
        future.thenAccept(consumer);
    }

    public void isOnline(BiConsumer<OfflinePlayer, Boolean> consumer) {
        future.thenAccept(offlinePlayer -> consumer.accept(offlinePlayer, offlinePlayer.isOnline()));
    }

    public void getUniqueId(BiConsumer<OfflinePlayer, UUID> consumer) {
        future.thenAccept(offlinePlayer -> consumer.accept(offlinePlayer, offlinePlayer.getUniqueId()));
    }

    public void isBanned(BiConsumer<OfflinePlayer, Boolean> consumer) {
        future.thenAccept(offlinePlayer -> consumer.accept(offlinePlayer, offlinePlayer.isBanned()));
    }

    public void setBanned(boolean b) {
        future.thenAccept(offlinePlayer -> offlinePlayer.setBanned(b));
    }

    public void isWhitelisted(BiConsumer<OfflinePlayer, Boolean> consumer) {
        future.thenAccept(offlinePlayer -> consumer.accept(offlinePlayer, offlinePlayer.isWhitelisted()));
    }

    public void setWhitelisted(boolean b) {
        future.thenAccept(offlinePlayer -> offlinePlayer.setWhitelisted(b));
    }

    public void getPlayer(Consumer<Player> consumer) {
        future.thenAccept(offlinePlayer -> consumer.accept(offlinePlayer.getPlayer()));
    }

    public void getFirstPlayed(BiConsumer<OfflinePlayer, Long> consumer) {
        future.thenAccept(offlinePlayer -> consumer.accept(offlinePlayer, offlinePlayer.getFirstPlayed()));
    }

    public void getLastPlayed(BiConsumer<OfflinePlayer, Long> consumer) {
        future.thenAccept(offlinePlayer -> consumer.accept(offlinePlayer, offlinePlayer.getLastPlayed()));
    }

    public void hasPlayedBefore(BiConsumer<OfflinePlayer, Boolean> consumer) {
        future.thenAccept(offlinePlayer -> consumer.accept(offlinePlayer, offlinePlayer.hasPlayedBefore()));
    }

    public void getBedSpawnLocation(BiConsumer<OfflinePlayer, Location> consumer) {
        future.thenAccept(offlinePlayer -> consumer.accept(offlinePlayer, offlinePlayer.getBedSpawnLocation()));
    }


    public void isOp(BiConsumer<OfflinePlayer, Boolean> consumer) {
        future.thenAccept(offlinePlayer -> consumer.accept(offlinePlayer, offlinePlayer.isOp()));
    }

    public void setOp(boolean b) {
        future.thenAccept(offlinePlayer -> offlinePlayer.setOp(b));
    }
}
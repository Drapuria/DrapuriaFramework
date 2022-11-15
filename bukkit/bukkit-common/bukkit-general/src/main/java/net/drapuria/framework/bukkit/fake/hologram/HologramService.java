package net.drapuria.framework.bukkit.fake.hologram;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.beans.annotation.*;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityService;
import net.drapuria.framework.bukkit.fake.hologram.repository.HologramRepository;
import net.drapuria.framework.bukkit.listener.events.EventSubscription;
import net.drapuria.framework.bukkit.listener.events.Events;
import net.drapuria.framework.util.Stacktrace;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service(name = "hologramService")
public class HologramService {

    private boolean isUseEventsForHologramHandling = true, oldUseEventsForHologramHandling = isUseEventsForHologramHandling;
    private final Set<EventSubscription<?>> subscribedEvents = new HashSet<>();
    private ScheduledFuture<?> scheduledFuture;
    private boolean destroying = false;

    @Getter
    private final HologramRepository hologramRepository = new HologramRepository();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("Hologram-Render-Pool-%d")
            .setUncaughtExceptionHandler((thread, exception) -> {
                Stacktrace.print(exception.getMessage(), exception);
            })
            .build());


    @PostInitialize
    public void init() {
        if (!isUseEventsForHologramHandling) {
            initScheduler();
        } else registerEvents();
        new BukkitRunnable() {
            @Override
            public void run() {
                checkPlayerBoundHolograms();
            }
        }.runTaskTimerAsynchronously(Drapuria.PLUGIN, 20 * 20, 20 * 30);
    }

    private long delay = 500;

    private void initScheduler() {
        if (this.destroying) return;
        scheduledFuture = executorService.scheduleWithFixedDelay(this::checkLoadableHolograms, 500, delay, TimeUnit.MILLISECONDS);
    }

    public void checkPlayerBoundHolograms() {
        if (
                ImmutableList.copyOf(hologramRepository.getGlobalHolograms()).stream().anyMatch(GlobalHologram::isLocationBoundToPlayer) ||
                        ImmutableList.copyOf(hologramRepository.getPlayerDefinedHolograms()).stream().anyMatch(PlayerDefinedHologram::isLocationBoundToPlayer) ||
                        ImmutableList.copyOf(hologramRepository.getPlayerHolograms()).stream().anyMatch(PlayerHologram::isLocationBoundToPlayer)) {
            if (this.isUseEventsForHologramHandling) {
                this.oldUseEventsForHologramHandling = this.isUseEventsForHologramHandling;
                this.unregisterEvents();
                this.isUseEventsForHologramHandling = false;
            }
            if (delay == 1) return;
            delay = 1;
            FrameworkMisc.TASK_SCHEDULER.runSync(() -> {
                if (scheduledFuture != null)
                    scheduledFuture.cancel(false);
                initScheduler();
            });
        } else {
            if (this.oldUseEventsForHologramHandling) {
                this.isUseEventsForHologramHandling = true;
                this.oldUseEventsForHologramHandling = false;
                if (scheduledFuture != null)
                    scheduledFuture.cancel(false);
                this.registerEvents();
            }
            if (delay == 50) return;
            delay = 50;
            if (!this.isUseEventsForHologramHandling) {
                FrameworkMisc.TASK_SCHEDULER.runSync(() -> {
                    scheduledFuture.cancel(false);
                    initScheduler();
                });
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

    @PostDestroy
    public void destroy() {
        this.destroying = true;
        setUseEventsForHologramHandling(false);
    }

    public boolean isUseEventsForHologramHandling() {
        return isUseEventsForHologramHandling;
    }

    public void setUseEventsForHologramHandling(boolean useEventsForHologramHandling) {
        if (this.isUseEventsForHologramHandling == useEventsForHologramHandling)
            return;
        isUseEventsForHologramHandling = useEventsForHologramHandling;
        if (this.isUseEventsForHologramHandling) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
            registerEvents();
        } else {
            unregisterEvents();
            initScheduler();
        }
    }

    private void onHologramsUpdate() {
        if (this.isUseEventsForHologramHandling)
            this.checkLoadableHolograms();
    }

    public void addHologram(final PlayerHologram playerHologram) {
        final Player player = playerHologram.getPlayer();
        this.hologramRepository.createPlayerHologramRepository(player);
        this.hologramRepository.getHolograms(player).add(playerHologram);
        if (this.isUseEventsForHologramHandling && player.isOnline())
            this.checkLoadableHolograms(player);
    }

    public void addHologram(final GlobalHologram globalHologram) {
        this.hologramRepository.getGlobalHolograms().add(globalHologram);
        this.onHologramsUpdate();
    }

    public void addHologram(final PlayerDefinedHologram playerDefinedHologram) {
        this.hologramRepository.getPlayerDefinedHolograms().add(playerDefinedHologram);
        this.onHologramsUpdate();
    }

    public void removeHologram(final PlayerHologram playerHologram) {
        final Player player = playerHologram.getPlayer();
        this.hologramRepository.getHolograms(player).remove(playerHologram);
        playerHologram.hide(player);
        if (player.isOnline())
            this.checkLoadableHolograms(player);
    }

    public void removeHologram(final GlobalHologram globalHologram) {
        this.hologramRepository.getGlobalHolograms().remove(globalHologram);
        globalHologram.destroy();
        this.onHologramsUpdate();
    }

    public void removeHologram(final PlayerDefinedHologram playerDefinedHologram) {
        this.hologramRepository.getPlayerDefinedHolograms().remove(playerDefinedHologram);
        playerDefinedHologram.destroy();
        this.onHologramsUpdate();
    }

    private void checkLoadableHolograms() {
        for (final Player player : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
            for (Hologram hologram : this.hologramRepository.getHolograms(player)) {
                if (hologram.isLocationBoundToPlayer())
                    hologram.updateBound();
                else
                    hologram.checkHologram();
            }
        }
        for (GlobalHologram globalHologram : this.hologramRepository.getGlobalHolograms()) {
            if (globalHologram.isLocationBoundToPlayer())
                globalHologram.updateBound();
            else
                globalHologram.checkHologram();
        }

        for (PlayerDefinedHologram playerDefinedHologram : this.hologramRepository.getPlayerDefinedHolograms()) {
            if (playerDefinedHologram.isLocationBoundToPlayer())
                playerDefinedHologram.updateBound();
            else
                playerDefinedHologram.checkHologram();
        }
    }

    private void checkLoadableHolograms(final Player player) {
        for (Hologram hologram : this.hologramRepository.getHolograms(player)) {
            hologram.checkHologram();
        }
        for (GlobalHologram globalHologram : this.hologramRepository.getGlobalHolograms()) {
            globalHologram.checkHologram(player);
        }
        for (PlayerDefinedHologram playerDefinedHologram : this.hologramRepository.getPlayerDefinedHolograms()) {
            playerDefinedHologram.checkHologram(player);
        }
    }

    private void unregisterEvents() {
        this.subscribedEvents.forEach(EventSubscription::unregister);
        this.subscribedEvents.clear();
    }

    private void registerEvents() {
        this.subscribedEvents.add(
                Events.subscribe(PlayerMoveEvent.class)
                        .listen(event -> {
                            final Location from = event.getFrom(), to = event.getTo();
                            if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ() && from.getBlockY() == to.getBlockY())
                                return;
                            final Player player = event.getPlayer();
                            final Chunk oldChunk = from.getChunk();
                            final Chunk newChunk = to.getChunk();
                            if (oldChunk.getWorld() != newChunk.getWorld() || oldChunk.getX() != newChunk.getZ() || oldChunk.getZ() != newChunk.getZ())
                                this.checkLoadableHolograms(player);
                        }).build(Drapuria.PLUGIN)
        );
        this.subscribedEvents.add(
                Events.subscribe(PlayerTeleportEvent.class)
                        .filter(event -> !event.isCancelled()
                                && (!event.getFrom().getWorld().equals(event.getTo().getWorld())
                                || event.getFrom().getChunk().getX() != event.getTo().getChunk().getX()
                                || event.getFrom().getChunk().getZ() != event.getTo().getChunk().getZ()))
                        .priority(EventPriority.MONITOR)
                        .listen(event -> DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> this.checkLoadableHolograms(event.getPlayer()), 10L))
                        .build(Drapuria.PLUGIN)
        );
    }
}
package net.drapuria.framework.bukkit.fake.entity;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.annotation.PostDestroy;
import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.fake.entity.exception.FakeEntityPoolAlreadyRegisteredException;
import net.drapuria.framework.bukkit.fake.entity.exception.FakeEntityPoolNotFoundException;
import net.drapuria.framework.bukkit.fake.entity.npc.NPC;
import net.drapuria.framework.bukkit.fake.entity.npc.NameTagType;
import net.drapuria.framework.bukkit.listener.events.Events;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.WrappedPacketOutScoreboardTeam;
import net.drapuria.framework.bukkit.protocol.protocollib.ProtocolLibService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service(name = "fakeEntityService")
@SuppressWarnings("SpellCheckingInspection")
public class FakeEntityService {

    public static FakeEntityService getService;

    private final Map<UUID, UUID> playersRandomId = new HashMap<>();

    private FakeEntityPool defaultPool;
    private final Map<String, FakeEntityPool> pools = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<Player, WrappedPacketOutScoreboardTeam> scoreboardTeamRegistry = new ConcurrentHashMap<>();
    private final Map<Player, WrappedPacketOutScoreboardTeam> halfScoreboardTeamRegistry = new ConcurrentHashMap<>();
    private final String scoreboardTeamName = UUID.randomUUID().toString().split("-")[0];
    private final String halfInvisibileScoreboardTeamName = UUID.randomUUID().toString().split("-")[0];
    @Getter
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("FakeEntity-Render-Pool-%d")
            .build());

    @PreInitialize
    public void init() {
        getService = this;
    }

    @SneakyThrows
    @PostInitialize
    public void registerDefaultPool() {
        this.registerPool(this.defaultPool = new FakeEntityPool(Drapuria.PLUGIN, DrapuriaCommon.METADATA_PREFIX + "POOL"));
    }

    @PostInitialize
    public void registerEvents() {
        Events
                .subscribe(PlayerJoinEvent.class)
                .priority(EventPriority.MONITOR)
                .listen(event -> {
                    final Player player = event.getPlayer();
                    cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 3000L);
                    this.playersRandomId.put(player.getUniqueId(), UUID.randomUUID());
                    this.executorService.submit(() -> {
                        WrappedPacketOutScoreboardTeam wrappedPacketOutScoreboardTeam = new WrappedPacketOutScoreboardTeam();
                        wrappedPacketOutScoreboardTeam.setName(scoreboardTeamName);
                        wrappedPacketOutScoreboardTeam.setVisibility(WrappedPacketOutScoreboardTeam.NameTagVisibility.NEVER);

                        WrappedPacketOutScoreboardTeam halfVisibilityWrappedPacketOutScoreboardTeam = new WrappedPacketOutScoreboardTeam();
                        final Scoreboard playerScoreboard = player.getScoreboard();
                        final Team playerScoreboardTeam = playerScoreboard == null ? null : playerScoreboard.getEntryTeam(player.getName());
                        final String halfVisibleTeamName = playerScoreboardTeam == null ? halfInvisibileScoreboardTeamName : playerScoreboardTeam.getName();
                        halfVisibilityWrappedPacketOutScoreboardTeam.setName(halfVisibleTeamName);
                        halfVisibilityWrappedPacketOutScoreboardTeam.setVisibility(WrappedPacketOutScoreboardTeam.NameTagVisibility.NEVER);
                        halfVisibilityWrappedPacketOutScoreboardTeam.setSeeFriendlyInvisibles(true);
                        if (playerScoreboardTeam != null) {
                            halfVisibilityWrappedPacketOutScoreboardTeam.setDisplayName(playerScoreboardTeam.getDisplayName());
                            halfVisibilityWrappedPacketOutScoreboardTeam.setPrefix(playerScoreboardTeam.getPrefix());
                            halfVisibilityWrappedPacketOutScoreboardTeam.setSuffix(playerScoreboardTeam.getSuffix());
                        }
                        halfScoreboardTeamRegistry.put(player, halfVisibilityWrappedPacketOutScoreboardTeam);
                        scoreboardTeamRegistry.put(player, wrappedPacketOutScoreboardTeam);
                        updateTeamForPlayer(player);
                    });
                })
                .build(Drapuria.PLUGIN);
        Events
                .subscribe(PlayerQuitEvent.class)
                .priority(EventPriority.MONITOR)
                .listen(event -> {
                    this.scoreboardTeamRegistry.remove(event.getPlayer());
                })
                .build(Drapuria.PLUGIN);
    }

    @PostDestroy
    public void stop() {
        pools.values().forEach(FakeEntityPool::shutdown);
        executorService.shutdown();
    }

    public FakeEntityPool getPool(final String name) {
        return this.pools.get(name);
    }

    public Collection<FakeEntityPool> getPools() {
        return this.pools.values();
    }

    public UUID getRandomIdOf(final UUID uuid) {
        return this.playersRandomId.get(uuid);
    }

    public void registerPool(final FakeEntityPool pool) throws FakeEntityPoolAlreadyRegisteredException {
        if (this.pools.containsKey(pool.getName()))
            throw new FakeEntityPoolAlreadyRegisteredException("FakeEntityPool with name '" + pool.getName() + "' already registered!");
        this.pools.put(pool.getName(), pool);
    }

    public FakeEntityPool getDefaultPool() {
        return this.defaultPool;
    }

    public void setDefaultPool(FakeEntityPool defaultPool) {
        this.defaultPool = defaultPool;
    }

    public void removePool(final FakeEntityPool pool) {
        this.pools.remove(pool.getName());
    }

    public boolean hasCooldown(final UUID player) {
        return this.cooldowns.containsKey(player) && this.cooldowns.get(player) > System.currentTimeMillis();
    }

    public WrappedPacketOutScoreboardTeam getScoreboardTeamPacket(final Player player) {
        return this.scoreboardTeamRegistry.get(player);
    }

    public WrappedPacketOutScoreboardTeam getHalfVisibleScoreboardTeamPacket(final Player player) {
        return this.halfScoreboardTeamRegistry.get(player);
    }

    public void updateTeamForPlayer(Player player) {
        if (!this.scoreboardTeamRegistry.containsKey(player)) return;
        List<String> namesToAdd = new ArrayList<>();
        for (FakeEntity entity : getPools().stream().flatMap(entityPool -> entityPool.getEntities().values().stream())
                .collect(Collectors.toSet())) {
            if (entity instanceof NPC &&
                    (((NPC) entity).getNpcOptions().getNameTagType() == NameTagType.HOLOGRAM ||
                            ((NPC) entity).getNpcOptions().getNameTagType().isHideHologram()) &&
                    !namesToAdd.contains(((NPC) entity).getGameProfile().getName())) {
                namesToAdd.add(((NPC) entity).getGameProfile().getName());
            }
        }
        if (!namesToAdd.isEmpty())
            executorService.submit(() -> {
                WrappedPacketOutScoreboardTeam packet = getScoreboardTeamPacket(player);
                packet.setNameSet(namesToAdd);
                ProtocolLibService.getService.sendPacket(player, packet.asProtocolLibPacketContainer());
            });
    }

    public void removeCooldown(final UUID player) {
        this.cooldowns.remove(player);
    }

    public void setCooldowns(final UUID player, long cooldown) {
        this.cooldowns.put(player, System.currentTimeMillis() + cooldown);
    }

    public void removePool(final String name) throws FakeEntityPoolNotFoundException {
        if (!this.pools.containsKey(name))
            throw new FakeEntityPoolNotFoundException("FakeEntityPool with name '" + name + "' does not exist!");
        this.removePool(this.getPool(name));
    }

    public int getFreeEntityId() {
        final AtomicInteger id = new AtomicInteger();
        do {
            id.set(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        } while (this.pools.values().stream().anyMatch(pool -> pool.getEntities().containsKey(id.get())));
        return id.get();
    }
}
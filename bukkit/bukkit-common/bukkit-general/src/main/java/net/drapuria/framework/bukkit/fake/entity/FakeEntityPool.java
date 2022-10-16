package net.drapuria.framework.bukkit.fake.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.fake.entity.event.PlayerFakeEntityInteractEvent;
import net.drapuria.framework.bukkit.player.DrapuriaPlayer;
import net.drapuria.framework.bukkit.player.PlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class FakeEntityPool {

    private static final FakeEntityService entityService = DrapuriaCommon.getBean(FakeEntityService.class);
    private static final PlayerRepository playerRepository = PlayerRepository.getRepository;

    private final Plugin holder;
    private final String name;

    private final Map<Integer, FakeEntity> entities = new ConcurrentHashMap<>();
    private Collection<FakeEntity> entityCollection = new ArrayList<>();
    private final String scoreboardTeamName = UUID.randomUUID().toString().split("-")[0];
    private long delayMillis = 300L;
    private double spawnDistance = 10;
    private double actionDistance = 6;
    private boolean updating = false;
    private final Queue<PlayerFakeEntityInteractEvent> eventQueue = new ConcurrentLinkedQueue<>();

    public FakeEntityPool(Plugin holder, String name) {
        this.holder = holder;
        this.name = name;
        this.registerInteractListener();
    }

    public void start() {
        entityService.getExecutorService().scheduleWithFixedDelay(this::tick, 100, 50, TimeUnit.MILLISECONDS);
    }

    public void updateTeamForPlayer(final Player player) {

    }

    public void updateEntityCollection() {
        this.updating = true;
        this.entityCollection = this.entities.values();
        this.updating = false;
    }

    private void tick() {
        handleEventQueue();
        if (this.updating) return;
        for (final Player player : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
            if (player.isDead()) continue;
            final Optional<DrapuriaPlayer> optDrapuriaPlayer = playerRepository.findById(player.getUniqueId());
            if (!optDrapuriaPlayer.isPresent())
                continue;
            final DrapuriaPlayer drapuriaPlayer = optDrapuriaPlayer.get();
            if (drapuriaPlayer.getSessionJoin() > System.currentTimeMillis() - 500) continue;
            for (final FakeEntity entity : this.entities.values()) {
                if (entity.isRespawning()) continue;
                boolean isShownFor = entity.isShownTo(player);
                if (!entity.getLocation().getWorld().equals(player.getWorld())) {
                    if (isShownFor) {
                        entity.hide(player);
                    }
                    continue;
                }
                final double distance = entity.getLocation().distance(player.getLocation());
                boolean inRange = distance <= this.spawnDistance;
                if (!inRange) {
                    if (isShownFor)
                        entity.hide(player);
                    continue;
                }
                if (isShownFor) {
                    if (distance <= this.actionDistance)
                        entity.tickActionForPlayer(player);
                    continue;
                }
                entity.show(player);
            }
        }

    }

    private void handleEventQueue() {
        if (!eventQueue.isEmpty()) {
            entityService.getExecutorService().submit(() -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        while (!eventQueue.isEmpty()) {
                            eventQueue.poll().call();
                        }
                    }
                }.runTask(this.holder);
            });
        }
    }

    private void registerInteractListener() {
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new PacketAdapter(this.holder, PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        final Player player = event.getPlayer();
                        if (entityService.hasCooldown(player.getUniqueId())) return;
                        final PacketContainer packetContainer = event.getPacket();
                        final int targetId = packetContainer.getIntegers().read(0);
                        final FakeEntity fakeEntity = FakeEntityPool.this.entities.get(targetId);
                        if (fakeEntity == null || fakeEntity.isRespawning()) return;
                        entityService.setCooldowns(player.getUniqueId(), 2000L);
                        EnumWrappers.EntityUseAction action = packetContainer.getEntityUseActions().read(0);
                        final PlayerFakeEntityInteractEvent fakeEntityInteractEvent = new PlayerFakeEntityInteractEvent(player, fakeEntity, action);
                        // TODO CHECK IF THE PACKET LISTENER EVEN IS ASYNC
                        if (fakeEntity.getOptions().isHandleEventAsync())
                            fakeEntityInteractEvent.call();
                        else
                            FakeEntityPool.this.eventQueue.add(fakeEntityInteractEvent);
                    }
                });
    }

}
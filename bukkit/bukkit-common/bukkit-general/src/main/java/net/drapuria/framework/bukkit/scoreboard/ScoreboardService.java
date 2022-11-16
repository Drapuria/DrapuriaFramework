/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.annotation.*;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.metadata.Metadata;
import net.drapuria.framework.bukkit.listener.events.Events;
import net.drapuria.framework.bukkit.scoreboard.board.DrapuriaBoard;
import net.drapuria.framework.bukkit.scoreboard.board.adapter.ScoreboardAdapter;
import net.drapuria.framework.bukkit.scoreboard.board.adapter.ScoreboardAdapterHolder;
import net.drapuria.framework.bukkit.scoreboard.board.impl.PacketDrapuriaBoard;
import net.drapuria.framework.bukkit.scoreboard.events.ScoreboardAdapterRemovedEvent;
import net.drapuria.framework.metadata.MetadataKey;
import net.drapuria.framework.metadata.MetadataMap;
import net.drapuria.framework.scheduler.factory.SchedulerFactory;
import net.drapuria.framework.scheduler.provider.ScheduledExecutorSchedulerProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.event.EventPriority.*;

@Service(name = "sidebar")
@Setter
@Getter
public class ScoreboardService {

    private static final MetadataKey<DrapuriaBoard> SCOREBOARD_KEY = MetadataKey
            .create(DrapuriaCommon.METADATA_PREFIX + "sidebar", DrapuriaBoard.class);
    private static final MetadataKey<ScoreboardAdapter> ADAPTER_KEY = MetadataKey
            .create(DrapuriaCommon.METADATA_PREFIX + "sidebar-adapter", ScoreboardAdapter.class);

    public static ScoreboardService getService;

    private ScoreboardAdapter defaultAdapter;
    private Class<? extends DrapuriaBoard> boardImplementation;
    private Constructor<?> boardImplementationConstructor;
    private final Map<ScoreboardAdapter, List<Player>> adapters = new HashMap<>();
    private final Map<ScoreboardAdapter, Long> ticksDown = new HashMap<>();


    @PreInitialize
    public void preInit() {
        getService = this;

        this.boardImplementation = PacketDrapuriaBoard.class;
        try {
            this.boardImplementationConstructor = this.boardImplementation
                    .getConstructor(SidebarOptions.class, Player.class, String.class);
        } catch (NoSuchMethodException ignored) {
        }
        ComponentRegistry.registerComponentHolder(new ScoreboardAdapterHolder());
    }

    @PostInitialize
    public void init() {
        Events.subscribe(PlayerQuitEvent.class)
                .priority(EventPriority.HIGHEST)
                .listen((subscription, event) -> {
                    final Player player = event.getPlayer();
                    MetadataMap metadataMap = Metadata.provideForPlayer(player);
                    metadataMap.get(ADAPTER_KEY).ifPresent(scoreboardAdapter -> {
                        List<Player> list = this.adapters.get(scoreboardAdapter);
                        if (list != null)
                            list.remove(player);
                    });
                    metadataMap.get(SCOREBOARD_KEY).ifPresent(DrapuriaBoard::remove);
                    metadataMap.remove(ADAPTER_KEY);
                    metadataMap.remove(SCOREBOARD_KEY);
                }).build(Drapuria.PLUGIN);

        Events.subscribe(PlayerJoinEvent.class)
                .priority(MONITOR)
                .listen((subscription, event) -> {

                    final Player player = event.getPlayer();
                    if (boardImplementationConstructor == null)
                        return;
                    try {
                        DrapuriaBoard board;
                        final MetadataMap metadataMap = Metadata.provideForPlayer(player);

                        metadataMap.put(SCOREBOARD_KEY, board = (DrapuriaBoard) boardImplementationConstructor
                                .newInstance(SidebarOptions.defaultOptions(),
                                        player,
                                        defaultAdapter == null ? null : defaultAdapter.getTitle(player)));
                        if (defaultAdapter == null)
                            return;
                        metadataMap.put(ADAPTER_KEY, defaultAdapter);
                        board.setLines(defaultAdapter.getLines(player));
                        board.setTitle(defaultAdapter.getTitle(player));
                        adapters.get(defaultAdapter).add(player);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }).build(Drapuria.PLUGIN);
        Events.subscribe(ScoreboardAdapterRemovedEvent.class)
                .priority(MONITOR)
                .filter(event -> event.getNewAdapter() != null)
                .listen(event -> setAdapter(event.getPlayer(), event.getNewAdapter()))
                .build(Drapuria.PLUGIN);
        new SchedulerFactory<Runnable>()
                .provider(ScheduledExecutorSchedulerProvider.class)
                .period(1)
                .delay(1)
                .supplier(() -> this::tick)
                .repeated((aLong, runnable) -> runnable.run())
                .build();
    }

    @PreDestroy
    public void shutdown() {

    }

    public void addAdapter(ScoreboardAdapter adapter) {
        this.ticksDown.put(adapter, 1L);
        this.adapters.put(adapter, new ArrayList<>());
    }

    public void removeAdapter(ScoreboardAdapter adapter) {
        this.adapters.remove(adapter);
        this.ticksDown.remove(adapter);
    }

    public void setAdapter(final Player player, final ScoreboardAdapter adapter) {
        if (adapter == null) {
            Metadata.provideForPlayer(player).put(ADAPTER_KEY, this.defaultAdapter);
            return;
        }
        Metadata.provideForPlayer(player).put(ADAPTER_KEY, adapter);
    }

    public void resetAdapter(final Player player) {
        this.setAdapter(player, null);
    }

    private void tick() {
        if (Drapuria.SHUTTING_DOWN)
            return;
        for (Map.Entry<ScoreboardAdapter, List<Player>> entry : ImmutableMap.copyOf(this.adapters).entrySet()) {
            final ScoreboardAdapter adapter = entry.getKey();
            long ticks = this.ticksDown.get(adapter);
            if (--ticks == 0) {
                entry.getValue().forEach(player -> {

                    Metadata.get(player)
                            .flatMap(metadataMap -> metadataMap.get(SCOREBOARD_KEY))
                            .ifPresent(drapuriaBoard ->
                            {
                                drapuriaBoard.setLines(adapter.getLines(player));
                                drapuriaBoard.setTitle(adapter.getTitle(player));
                            });
                });
                ticks = adapter.getTickTime();
            }
            this.ticksDown.put(adapter, ticks);
        }
    }

    public void clearAdapter(ScoreboardAdapter adapter) {
        ImmutableList.copyOf(Bukkit.getOnlinePlayers()).forEach(player -> {
            Metadata.provideForPlayer(player).get(ADAPTER_KEY)
                    .ifPresent(scoreboardAdapter -> {
                        if (scoreboardAdapter == adapter) {
                            Metadata.provideForPlayer(player).get(SCOREBOARD_KEY)
                                    .ifPresent(DrapuriaBoard::remove);
                            Metadata.provideForPlayer(player).remove(ADAPTER_KEY);
                            new ScoreboardAdapterRemovedEvent(player, scoreboardAdapter).call();
                        }
                    });
        });
    }
}
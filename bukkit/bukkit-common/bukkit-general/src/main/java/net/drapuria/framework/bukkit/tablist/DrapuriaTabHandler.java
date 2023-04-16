package net.drapuria.framework.bukkit.tablist;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.annotation.Autowired;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.impl.metadata.Metadata;
import net.drapuria.framework.bukkit.protocol.packet.PacketDto;
import net.drapuria.framework.bukkit.protocol.packet.PacketListener;
import net.drapuria.framework.bukkit.protocol.packet.PacketService;
import net.drapuria.framework.bukkit.protocol.packet.type.PacketTypeClasses;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutLogin;
import net.drapuria.framework.bukkit.reflection.BukkitReflection;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.minecraft.MinecraftVersion;
import net.drapuria.framework.bukkit.tablist.util.IDrapuriaTab;
import net.drapuria.framework.bukkit.tablist.util.impl.ProtocolLibTabImpl;
import net.drapuria.framework.bukkit.util.AsyncTypeCallback;
import net.drapuria.framework.metadata.MetadataKey;
import net.drapuria.framework.metadata.MetadataMap;
import net.drapuria.framework.util.Stacktrace;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class DrapuriaTabHandler {

    //Instance
    @Getter
    private static DrapuriaTabHandler instance;

    public static final MetadataKey<DrapuriaTablist> TABLIST_KEY = MetadataKey.create("Drapuria_" + "TabList", DrapuriaTablist.class);
    private static final MetadataKey<SimpleHeaderFooter> SIMPLE_HEADER_FOOTER_KEY = MetadataKey.create("Drapuria_" + "SimpleHeaderFooter", SimpleHeaderFooter.class);


    private final DrapuriaTabAdapter adapter;
    private ScheduledExecutorService thread;
    private IDrapuriaTab implementation;

    @Autowired
    private PacketService packetService;

    //Tablist Ticks
    @Setter
    private long ticks = 20;

    private long t = 0;

    public DrapuriaTabHandler(DrapuriaTabAdapter adapter) {
        if (instance != null) {
            instance = null;
            System.out.println("Tablist registered twice, be careful.");
        }

        instance = this;

        this.adapter = adapter;

        DrapuriaCommon.injectBean(this);

        this.registerImplementation();
        this.setup();
    }

    private void registerImplementation() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            this.implementation = new ProtocolLibTabImpl();
            return;
        }
    }

    public void registerPlayerTablist(Player player) {
        if (Metadata.provideForPlayer(player).has(SIMPLE_HEADER_FOOTER_KEY))
            removePlayerTablist(player);
        DrapuriaTablist tablist = new DrapuriaTablist(player);
        Metadata
                .provideForPlayer(player)
                .put(TABLIST_KEY, tablist);

    }

    public void removePlayerTablist(Player player) {
        new AsyncTypeCallback<MetadataMap>(data -> {
            if (data != null) {
                data.getOrNull(TABLIST_KEY).setDestroying(false);
                data.remove(TABLIST_KEY);
            }
        }).performSync(true, () -> {
            MetadataMap map = Metadata.provideForPlayer(player);
            map.get(TABLIST_KEY).ifPresent(DrapuriaTablist::destroy);
            return map.has(TABLIST_KEY) ? map : null;
        });
    }


    public void registerPlayerSimpleHeaderFooter(Player player) {
        SimpleHeaderFooter headerFooter = new SimpleHeaderFooter(player);
        Metadata
                .provideForPlayer(player)
                .put(SIMPLE_HEADER_FOOTER_KEY, headerFooter);
    }

    public void removeSimpleHeaderFooter(Player player) {
        new AsyncTypeCallback<MetadataMap>(data -> {
            if (data != null) {
                data.getOrNull(SIMPLE_HEADER_FOOTER_KEY).setDestroying(false);
                data.remove(SIMPLE_HEADER_FOOTER_KEY);
            }
        }).performSync(true, () -> {
            MetadataMap map = Metadata.provideForPlayer(player);
            map.get(SIMPLE_HEADER_FOOTER_KEY).ifPresent(SimpleHeaderFooter::destroy);
            return map.has(SIMPLE_HEADER_FOOTER_KEY) ? map : null;
        });
    }

    private void setup() {

        //Ensure that the thread has stopped running
        if (this.thread != null) {
            this.thread.shutdown();
            this.thread = null;
        }

        // To ensure client will display 60 slots on 1.7
        if (Bukkit.getMaxPlayers() < 60) {
            if (MinecraftVersion.VERSION.olderThan(Minecraft.Version.v1_8_R1)) {
                packetService.registerPacketListener(new PacketListener() {
                    @Override
                    public Class<?>[] type() {
                        return new Class[]{PacketTypeClasses.Server.LOGIN};
                    }

                    @Override
                    public boolean write(Player player, PacketDto dto) {
                        WrappedPacketOutLogin packet = dto.wrap(WrappedPacketOutLogin.class);
                        packet.setMaxPlayers(60);

                        dto.refresh();
                        return true;
                    }
                });
            } else {
                BukkitReflection.setMaxPlayers(Drapuria.PLUGIN.getServer(), 60);
            }
        }

        //Start Thread
        this.thread = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                .setNameFormat("Drapuria-Tablist-Thread")
                .setDaemon(true)
                .setUncaughtExceptionHandler((thread1, throwable) -> Stacktrace.print(throwable))
                .build());

        this.thread.scheduleAtFixedRate(() -> {
            if (this.t < this.ticks) {
                this.t++;
                return;
            }
            this.t = 0;
            for (Player player : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
                DrapuriaTablist tablist = Metadata
                        .provideForPlayer(player)
                        .getOrNull(TABLIST_KEY);

                if (tablist != null) {
                    try {
                        tablist.update();
                    } catch (Throwable throwable) {
                        Stacktrace.print(throwable);
                    }
                } else {
                    SimpleHeaderFooter headerFooter = Metadata
                            .provideForPlayer(player)
                            .getOrNull(SIMPLE_HEADER_FOOTER_KEY);
                    if (headerFooter != null)
                        headerFooter.update();
                }
            }
        }, 50L, 50L, TimeUnit.MILLISECONDS);
    }

    public void stop() {

        if (this.thread != null) {
            this.thread.shutdown();
            this.thread = null;
        }

    }
}

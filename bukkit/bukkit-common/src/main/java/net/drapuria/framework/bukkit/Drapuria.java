package net.drapuria.framework.bukkit;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.annotation.Autowired;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.drapuria.framework.bukkit.impl.*;
import net.drapuria.framework.bukkit.impl.command.provider.BukkitCommandProvider;
import net.drapuria.framework.bukkit.impl.module.scanners.PluginDependenciesScanner;
import net.drapuria.framework.bukkit.impl.scheduler.provider.BukkitSchedulerProvider;
import net.drapuria.framework.bukkit.impl.server.ServerImplementation;
import net.drapuria.framework.bukkit.messaging.BungeeMessaging;
import net.drapuria.framework.bukkit.util.SpigotUtil;
import net.drapuria.framework.command.service.CommandService;
import net.drapuria.framework.module.service.ModuleService;
import net.drapuria.framework.plugin.PluginClassLoader;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.random.FastRandom;
import net.drapuria.framework.scheduler.action.RepeatedAction;
import net.drapuria.framework.scheduler.factory.SchedulerFactory;
import org.apache.http.util.Asserts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Bukkit Framework Main class
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Drapuria {

    public static final Logger LOGGER = LogManager.getLogger(Drapuria.class);
    public static Plugin PLUGIN;
    public static ServerImplementation IMPLEMENTATION;
    public static FastRandom RANDOM;
    public static PluginClassLoader CLASS_LOADER;
    public static boolean SHUTTING_DOWN = false;

    @Autowired
    public static CommandService getCommandService;
    public static BukkitCommandProvider getCommandProvider;

    public static void preInit() {
        PluginManager.initialize(new BukkitPluginHandler());
    }

    /**
     * @param plugin The BukkitPlugin managing this framework
     */
    public static void init(Plugin plugin) {
        Asserts.check(Drapuria.PLUGIN == null, "Drapuria already initiated.");

        Drapuria.PLUGIN = plugin;
        Drapuria.CLASS_LOADER = new PluginClassLoader(plugin.getClass().getClassLoader());
        Drapuria.RANDOM = new FastRandom();
        BungeeMessaging.init();

        SpigotUtil.init();
        Drapuria.initCommon();
        getCommandProvider = (BukkitCommandProvider) getCommandService.getCommandProvider();
        final ModuleService moduleService = (ModuleService) DrapuriaCommon.BEAN_CONTEXT.getBean(ModuleService.class);
        moduleService.registerScanner(PluginDependenciesScanner.class);
        // load internal modules (modules managed by the framework)
        DrapuriaCommon.TASK_SCHEDULER.runSync(moduleService::loadInternalModules);
        /*
        new BukkitRunnable() {
            int i = 0;
            long lastTick1 = 0;
            long lastTick2 = 0;
            @Override
            public void run() {
                AbstractSchedulerFactory<Player, ?> factory = (BukkitSchedulerFactory<Player>) new BukkitSchedulerFactory<Player>()
                        .delay(200)
                        .period(20)
                        .supplier(() -> Bukkit.getPlayer("marinus1111"))
                        .iterations(50)
                        .at(Timestamp.END, player -> player.sendMessage("ENDE"))
                        .repeated(new RepeatedAction<>(false, true, -1, -1, (aLong, player) -> {
                            Bukkit.broadcastMessage(String.valueOf(System.currentTimeMillis() - lastTick1));
                            lastTick1 = System.currentTimeMillis();
                        }));
                factory.build();
            }
        }.runTaskLater(PLUGIN, 20 * 5);

*/


        AtomicInteger i = new AtomicInteger(0);

        new BukkitRunnable() {
            @Override
            public void run() {
                new SchedulerFactory<Server>()
                        .delay(50)
                        .iterations(-1)
                        .period(20)
                        .provider(BukkitSchedulerProvider.class)
                        .supplier(Bukkit::getServer)
                        .repeated(new RepeatedAction<>(true, true, true, 0, 0,
                                (aLong, server) -> {
                                    server.broadcastMessage("Hallo");
                                }))
                        .build();
            }
        }.runTaskLater(PLUGIN, 20 * 5);
    }

    @SneakyThrows
    public static void shutdown() {
        SHUTTING_DOWN = true;
        DrapuriaCommon.shutdown();
        PluginManager.INSTANCE.callFrameworkFullyDisable();
    }

    private static void initCommon() {
        ComponentRegistry.registerComponentHolder(new ComponentHolderBukkitListener());
        DrapuriaCommon.builder()
                .platform(new BukkitDrapuriaPlatform())
                .eventHandler(new BukkitEventHandler())
                .taskScheduler(new BukkitTaskScheduler(PLUGIN))
                .init();
    }

    public static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            Plugin plugin = null;
            try {
                plugin = JavaPlugin.getProvidingPlugin(listener.getClass());
            } catch (Throwable ignored) {
            }
            if (plugin == null)
                plugin = Drapuria.PLUGIN;
            if (!plugin.isEnabled()) {
                Drapuria.LOGGER.error("The plugin is not enabled but trying to register listener.");
                return;
            }
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    public void unregisterEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }

    public static void callEvent(Event event) {
        PLUGIN.getServer().getPluginManager().callEvent(event);
    }
}

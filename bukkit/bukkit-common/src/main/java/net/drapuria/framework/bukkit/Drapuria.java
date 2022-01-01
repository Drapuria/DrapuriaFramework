package net.drapuria.framework.bukkit;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.impl.*;
import net.drapuria.framework.bukkit.impl.command.provider.BukkitCommandProvider;
import net.drapuria.framework.bukkit.impl.module.scanners.PluginDependenciesScanner;
import net.drapuria.framework.bukkit.impl.server.ServerImplementation;
import net.drapuria.framework.bukkit.util.SpigotUtil;
import net.drapuria.framework.command.service.CommandService;
import net.drapuria.framework.module.service.ModuleService;
import net.drapuria.framework.plugin.PluginClassLoader;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.random.FastRandom;
import net.drapuria.framework.services.Autowired;
import net.drapuria.framework.services.ComponentRegistry;
import org.apache.http.util.Asserts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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

        SpigotUtil.init();
        Drapuria.initCommon();
        getCommandProvider = (BukkitCommandProvider) getCommandService.getCommandProvider();

        final ModuleService moduleService = (ModuleService) DrapuriaCommon.BEAN_CONTEXT.getBean(ModuleService.class);
        moduleService.registerScanner(PluginDependenciesScanner.class);
        // load internal modules (modules managed by the framework)
        DrapuriaCommon.TASK_SCHEDULER.runSync(moduleService::loadInternalModules);
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
            } catch (Throwable ignored) { }
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

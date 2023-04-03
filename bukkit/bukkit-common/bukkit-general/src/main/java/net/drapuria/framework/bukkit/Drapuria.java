/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import net.drapuria.framework.BootstrapInvoke;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.beans.annotation.Autowired;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.drapuria.framework.beans.details.SimpleBeanDetails;
import net.drapuria.framework.bukkit.configuration.BukkitDrapuriaConfiguration;
import net.drapuria.framework.bukkit.impl.*;
import net.drapuria.framework.bukkit.impl.command.provider.BukkitCommandProvider;
import net.drapuria.framework.bukkit.impl.metadata.Metadata;
import net.drapuria.framework.bukkit.impl.module.scanners.PluginDependenciesScanner;
import net.drapuria.framework.bukkit.impl.server.ServerImplementation;
import net.drapuria.framework.bukkit.inventory.anvil.AbstractVirtualAnvil;
import net.drapuria.framework.bukkit.item.skull.impl.HDBRepository;
import net.drapuria.framework.bukkit.messaging.BungeeMessaging;
import net.drapuria.framework.bukkit.util.SpigotUtil;
import net.drapuria.framework.command.service.CommandService;
import net.drapuria.framework.language.LanguageService;
import net.drapuria.framework.module.service.ModuleService;
import net.drapuria.framework.plugin.PluginClassLoader;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.random.FastRandom;
import net.drapuria.framework.util.MethodAnnotationScanner;
import net.drapuria.framework.util.Stacktrace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.UUID;

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
    public static BukkitDrapuriaConfiguration drapuriaConfiguration;

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
        if (Drapuria.PLUGIN != null) {
            try {
                throw new Exception("Drapuria already initiated.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Drapuria.PLUGIN = plugin;
        Drapuria.CLASS_LOADER = new PluginClassLoader(plugin.getClass().getClassLoader());
        Drapuria.RANDOM = new FastRandom();
        BungeeMessaging.init();
        SpigotUtil.init();
        Drapuria.initCommon();
        BeanContext.INSTANCE.registerBean(new SimpleBeanDetails(Bukkit.getServer(), "Server", Server.class));
        Drapuria.drapuriaConfiguration = new BukkitDrapuriaConfiguration();
        IMPLEMENTATION = ServerImplementation.load(BeanContext.INSTANCE);
        AbstractVirtualAnvil.load();
        getCommandProvider = (BukkitCommandProvider) getCommandService.getCommandProvider();
        LanguageService.getService.setLocalizedMessageClass(LocalizedMessage.class);
        final ModuleService moduleService = (ModuleService) DrapuriaCommon.BEAN_CONTEXT.getBean(ModuleService.class);
        moduleService.registerScanner(PluginDependenciesScanner.class);
        // load internal modules (modules managed by the framework)
        DrapuriaCommon.TASK_SCHEDULER.runSync(moduleService::loadInternalModules);
        MethodAnnotationScanner scanner = new MethodAnnotationScanner(BootstrapInvoke.class);
        scanner.getResults().values().forEach(methods -> Arrays.stream(methods).forEach(method -> {
            try {
                method.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Stacktrace.print(e);
            }
        }));
        if (drapuriaConfiguration.getDevelopmentConfiguration().getRestartIfUpdateFolderNotEmpty().isEnabled())
            startUpdateFolderChecker();
        Metadata.provideForPlayer(UUID.randomUUID());
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
        try {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onDatabaseLoad(final DatabaseLoadEvent event) {
                    DrapuriaCommon.getBean(HDBRepository.class).processQueuedIds();
                }
            }, PLUGIN);
        } catch (Exception ignored) {
            LOGGER.warn("HDB repository will not work. No HeadDatabase Plugin installed!");
        }
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
            try {
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
            } catch (Exception e) {
                Stacktrace.print("Could not fully register " + listener.getClass().getName(), e);
            }
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

    private static void startUpdateFolderChecker() {
        long delay = drapuriaConfiguration.getDevelopmentConfiguration().getRestartIfUpdateFolderNotEmpty().getCheckDelay();
        if (delay < 400)
            delay = 400;
        DrapuriaCommon.TASK_SCHEDULER.runRepeated(() -> {
            if (isJarFileInsideUpdateFolder())
                DrapuriaCommon.PLATFORM.shutdown();

        }, delay, drapuriaConfiguration.getDevelopmentConfiguration().getRestartIfUpdateFolderNotEmpty().getCheckDelay());
    }

    private static boolean isJarFileInsideUpdateFolder() {
        final File updateFolder = new File("plugins/update");
        if (!updateFolder.exists()) {
            if (!updateFolder.mkdir())
                return false;
        }
        final File[] files = updateFolder.listFiles();
        if (files == null || files.length == 0)
            return false;
        if (Arrays.stream(files).anyMatch(file -> !file.getName().endsWith(".jar")))
            return false;
        return (Arrays.stream(files).allMatch(file -> file.getName().endsWith(".jar")));
    }
}

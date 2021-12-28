package net.drapuria.framework.module.service;

import lombok.Getter;
import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.annotations.ModuleData;
import net.drapuria.framework.module.classloader.ModuleClassLoader;
import net.drapuria.framework.module.parent.ModuleParent;
import net.drapuria.framework.module.parent.PlatformBasedParent;
import net.drapuria.framework.module.repository.GlobalModuleRepository;
import net.drapuria.framework.module.repository.ModuleRepository;
import net.drapuria.framework.module.repository.PlatformModuleRepository;
import net.drapuria.framework.module.scanner.EnvironmentScanner;
import net.drapuria.framework.module.scanner.ModuleDependenciesScanner;
import net.drapuria.framework.module.scanner.data.EnvironmentScanMissingDependenciesData;
import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.plugin.PluginListenerAdapter;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.services.PostInitialize;
import net.drapuria.framework.services.PreDestroy;
import net.drapuria.framework.services.PreInitialize;
import net.drapuria.framework.services.Service;
import net.drapuria.framework.util.Stacktrace;
import net.drapuria.framework.util.cycle.Cycle;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Service(name = "Modules")
public class ModuleService {

    private final Set<Class<? extends EnvironmentScanner>> scanners = new HashSet<>();

    public static ModuleService getInstance;

    @Getter
    private final Map<ModuleParent<?>, ModuleRepository<?>> repositories = new HashMap<>();
    private final File dataFolder = new File(DrapuriaCommon.PLATFORM.getDataFolder(), "modules");
    private final Logger logger = DrapuriaCommon.getLogger();

    private PlatformModuleRepository platformModuleRepository;
    private GlobalModuleRepository globalModuleRepository;

    private boolean internalModulesLoader = false;

    @PreInitialize
    public void preInit() {
        getInstance = this;
    }

    @PostInitialize
    public void init() {
        if (!dataFolder.exists())
            dataFolder.mkdir();
        registerScanner(ModuleDependenciesScanner.class);
        this.globalModuleRepository = new GlobalModuleRepository(this);
        platformModuleRepository = new PlatformModuleRepository(new PlatformBasedParent());
        DrapuriaCommon.TASK_SCHEDULER.runScheduled(this::unloadModules, 20 * 20);
    }

    @PreDestroy
    public void disable() {
        logger.info("[Drapuria-Modules] Disabling Modules...");
        unloadModules();
        logger.info("[Drapuria-Modules] Disabled all Modules!");
    }

    public GlobalModuleRepository getGlobalModuleRepository() {
        return globalModuleRepository;
    }

    public void registerScanner(Class<? extends EnvironmentScanner> scannerClass) {
        this.scanners.add(scannerClass);
    }

    public void unregisterScanner(Class<? extends EnvironmentScanner> scannerClass) {
        this.scanners.remove(scannerClass);
    }

    public void loadInternalModules() {
        if (internalModulesLoader)
            return;
        loadModules(dataFolder, new PlatformBasedParent(), platformModuleRepository, true);
        internalModulesLoader = true;
    }

    public void loadModules(File base, ModuleParent<?> parent, ModuleRepository<?> repository) {
        if (!internalModulesLoader) {
            DrapuriaCommon.TASK_SCHEDULER.runScheduled(() -> {
                loadModules(base, parent, repository);
            }, 20);
            return;
        }
        try {
            loadModules(base, parent, repository, true);
        } catch (Exception e) {
            Stacktrace.print(e);
        }
    }

    private void loadModules(File base, ModuleParent<?> parent, ModuleRepository<?> repository, boolean log) {
        if (!base.exists())
            base.mkdirs();
        if (log)
            logger.info("[Drapuria-Modules] Searching for modules in " + base.getPath());
        final File[] possibleModules = Arrays.stream(Objects.requireNonNull(base
                .listFiles(pathName -> pathName.getName().endsWith(".jar"))))
                .filter(Objects::nonNull)
                .filter(this::isModule).toArray(File[]::new);
        final List<ModuleAdapter> moduleAdapters = new ArrayList<>();
        final List<ModuleAdapter> toEnable = new ArrayList<>();
        final List<EnvironmentScanMissingDependenciesData> invalidDependencies = new ArrayList<>();
        for (File file : possibleModules) {
            try {
                ModuleClassLoader classLoader = parent.createModuleClassLoader(file, this);
                ModuleAdapter adapter;
                adapter = loadModule(classLoader);
                moduleAdapters.add(adapter);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if (log)
            logger.info("[Drapuria-Modules] " + moduleAdapters.size() + " modules found.");
        List<ModuleAdapter> adapterCycle = (moduleAdapters.stream()
                .sorted(Comparator.comparingInt(o -> o.getModuleData().name().length()))
                .collect(Collectors.toList()));

        int tries = 0;
        final Set<ModuleAdapter> skipList = new HashSet<>();
        do {
            adapterLoop:
            for (ModuleAdapter moduleAdapter : adapterCycle) {
                if (skipList.contains(moduleAdapter) || toEnable.contains(moduleAdapter))
                    continue;
                for (Class<? extends EnvironmentScanner> scanner : scanners) {
                    EnvironmentScanner environmentScanner = constructScanner(scanner, moduleAdapter);
                    environmentScanner.validateDependencies();
                    if (environmentScanner.getMissingDependenciesData() != null) {
                        invalidDependencies.add(environmentScanner.getMissingDependenciesData());
                        skipList.add(moduleAdapter);
                        continue adapterLoop;
                    }
                }
                toEnable.add(moduleAdapter);
                moduleAdapter.getModule().onLoad();
                DrapuriaCommon.PLATFORM.getLogger().info("[Drapuria-Modules] Loaded " + moduleAdapter.getModuleData().name());
            }
            adapterCycle.removeAll(toEnable);
            tries++;
        } while (tries < 30 && !adapterCycle.isEmpty());
        invalidDependencies.forEach(scanData -> {
            logger.error("[Drapuria-Modules] " + "Missing Dependencies for module "
                    + scanData.getAdapter().getModuleData().name() + ": "
                    + Arrays.stream(scanData.getMissingDependencies())
                    .map(missingDependencyData ->
                            missingDependencyData.getName() + " (" + missingDependencyData.getType() + ")")
                    .collect(Collectors.joining(", ")));
        });
        invalidDependencies.forEach(scanData -> scanData.getAdapter().getClassLoader().unload());
        if (toEnable.isEmpty()) {
            if (log)
                logger.info("[Drapuria-Modules] No Modules loaded");
            return;
        }

        tries = 0;
        final List<ModuleAdapter> bootOrder = new ArrayList<>();
        do {
            Iterator<ModuleAdapter> iterator = toEnable.iterator();
            whileLoop:
            while (iterator.hasNext()) {
                ModuleAdapter moduleAdapter = iterator.next();
                for (Class<? extends EnvironmentScanner> scanner : scanners) {
                    EnvironmentScanner environmentScanner = constructScanner(scanner, moduleAdapter);
                    environmentScanner.scan();
                    if (!environmentScanner.canEnable()) {
                        continue whileLoop;
                    }
                }
                iterator.remove();
                bootOrder.add(moduleAdapter);
                addModuleToRepository(repository, moduleAdapter);
                tries++;
            }
        } while (tries < 5 && !toEnable.isEmpty());

        for (ModuleAdapter moduleAdapter : bootOrder) {
            logger.info("[Drapuria-Modules] Enabling " + moduleAdapter.getModuleData().name()
                    + " "
                    + moduleAdapter.getModuleData().version() + "...");
            moduleAdapter.getModule().onEnable();
        }
    }

    private void unloadModules() {
        globalModuleRepository.getModules().forEach(module -> unloadModule(module.getName()));
    }

    public boolean unloadModule(final String name) {
        final Module module = globalModuleRepository.findByName(name);
        if (module == null) {
            logger.error("[Drapuria-Modules] No Module to unload with name " + name + " found!");
            return false;
        }
        final ModuleAdapter moduleAdapter = globalModuleRepository.getAdapterFromModule(module);
        module.onDisable();
        if (moduleAdapter != null)
            moduleAdapter.getClassLoader().unload();
        globalModuleRepository.removeModule(module);
        return true;
    }

    private void addModuleToRepository(ModuleRepository<?> repository, ModuleAdapter adapter) {
        if (!this.repositories.containsKey(adapter.getModule().getModuleParent())) {
            this.repositories.put(adapter.getModule().getModuleParent(), repository);
        }
        this.repositories.get(adapter.getModule().getModuleParent()).save(adapter);
    }

    @SneakyThrows
    public EnvironmentScanner constructScanner(Class<? extends EnvironmentScanner> scanner, ModuleAdapter adapter) {
        return scanner.getConstructor(ModuleService.class, ModuleAdapter.class).newInstance(this, adapter);
    }

    private ModuleAdapter loadModule(final ModuleClassLoader moduleClassLoader) {
        return moduleClassLoader.load();
    }

    private boolean isModule(final File file) {
        final JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            return false;
        }
        boolean isModule = jarFile.stream().anyMatch(entry -> {
            ModuleData data;
            try {
                data = Class.forName(entry.getName().replace(".class", "")
                                .replace("/", "."),
                        false, new URLClassLoader(new URL[]{file.toURI().toURL()},
                                this.getClass().getClassLoader()))
                        .getAnnotation(ModuleData.class);
            } catch (ClassNotFoundException | MalformedURLException e) {
                return false;
            }
            return data != null;
        });
        try {
            jarFile.close();
        } catch (IOException ignored) {
        }
        return isModule;
    }

    private ModuleData getDataFromModule(final File file) {
        final JarFile jarFile;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            return null;
        }
        AtomicReference<ModuleData> moduleData = new AtomicReference<>();
        jarFile.stream().forEach(entry -> {
            ModuleData data;
            try {
                data = Class.forName(entry.getName().replace(".class", "")
                                .replace("/", "."),
                        false, new URLClassLoader(new URL[]{file.toURI().toURL()},
                                this.getClass().getClassLoader()))
                        .getAnnotation(ModuleData.class);
            } catch (ClassNotFoundException | MalformedURLException e) {
                return;
            }
            moduleData.set(data);
        });
        try {
            jarFile.close();
        } catch (IOException ignored) {
        }
        return moduleData.get();
    }
}

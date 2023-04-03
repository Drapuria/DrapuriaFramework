/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.beans;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.NonNull;
import net.drapuria.framework.FrameworkMisc;
import net.drapuria.framework.NonNullArrayList;
import net.drapuria.framework.libraries.annotation.MavenDependency;
import net.drapuria.framework.libraries.annotation.MavenRepository;
import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.plugin.PluginListenerAdapter;
import net.drapuria.framework.plugin.PluginManager;
import net.drapuria.framework.beans.annotation.*;
import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.drapuria.framework.beans.details.*;
import net.drapuria.framework.beans.details.constructor.BeanParameterDetailsMethod;
import net.drapuria.framework.beans.exception.ServiceAlreadyExistsException;
import net.drapuria.framework.util.AccessUtil;
import net.drapuria.framework.util.SimpleTiming;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imanity.framework.reflect.Reflect;
import org.imanity.framework.reflect.ReflectLookup;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


@SuppressWarnings({"unused", "UnusedReturnValue"})
@MavenDependency(groupId = "org{}imanity{}framework", artifactId = "reflect", versionPackage = "0.0.1-SNAPSHOT", version = "0.0.1-20210415.103015-6",
        repo = @MavenRepository(url = "https://maven.imanity.dev/repository/imanity-libraries/"))
public class BeanContext {

    public static boolean SHOW_LOGS = true;
    public static BeanContext INSTANCE;
    public static final int PLUGIN_LISTENER_PRIORITY = 100;

    /**
     * Logging
     */
    public static final Logger LOGGER = LogManager.getLogger(BeanContext.class);

    protected static void log(String msg, Object... replacement) {
        if (SHOW_LOGS) {
            LOGGER.info("[BeanContext] " + String.format(msg, replacement));
        }
    }

    protected static SimpleTiming logTiming(String msg) {
        return SimpleTiming.create(time -> log("Ended %s - took %d ms", msg, time));
    }

    /**
     * Lookup Storages
     */
    private final Map<Class<?>, BeanDetails> beanByType = new ConcurrentHashMap<>();
    private final Map<String, BeanDetails> beanByName = new ConcurrentHashMap<>();

    /**
     * NOT THREAD SAFE
     */
    private final List<BeanDetails> sortedBeans = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Initializing Method for Bean Context
     */
    public void init() {
        INSTANCE = this;

        this.registerBean(new SimpleBeanDetails(this, "beanContext", this.getClass()));
        log("BeanContext has been registered as bean.");

        ComponentRegistry.registerComponentHolders();
        try {
            this.scanClasses("framework", BeanContext.class.getClassLoader(), Collections.singleton("net.drapuria.framework"));
        } catch (Throwable throwable) {
            LOGGER.error("Error while scanning classes for framework", throwable);
            FrameworkMisc.PLATFORM.shutdown();
            return;
        }

        if (PluginManager.isInitialized()) {
            log("Find PluginManager, attempt to register Plugin Listeners");

            PluginManager.INSTANCE.registerListener(new PluginListenerAdapter() {
                @Override
                public void onPluginEnable(AbstractPlugin plugin) {
                    BeanDetails beanDetails = new SimpleBeanDetails(plugin, plugin.getName(), plugin.getClass());

                    try {
                        beanDetails.bindWith(plugin);
                        registerBean(beanDetails, false);
                        log("Plugin " + plugin.getName() + " has been registered as bean.");
                    } catch (Throwable throwable) {
                        LOGGER.error("An error occurs while registering plugin", throwable);
                        plugin.close();
                        return;
                    }

                    try {
                        scanClasses(plugin.getName(), plugin.getPluginClassLoader(), findClassPaths(plugin.getClass()), beanDetails);
                    } catch (Throwable throwable) {
                        LOGGER.error("An error occurs while handling scanClasses()", throwable);
                        plugin.close();
                    }
                }

                @Override
                public void onPluginDisable(AbstractPlugin plugin) {
                    Collection<BeanDetails> beanDetailsList = findDetailsBindWith(plugin);
                    try {
                        call(PreDestroy.class, beanDetailsList);
                    } catch (Throwable throwable) {
                        LOGGER.error(throwable);
                    }

                    beanDetailsList.forEach(details -> {
                        log("Bean " + details.getName() + " Disabled, due to plugin " + plugin.getName() + " disabled.");

                        try {
                            details.onDisable();
                            unregisterBean(details);
                        } catch (Throwable throwable) {
                            LOGGER.error(throwable);
                        }
                    });

                    try {
                        call(PostDestroy.class, beanDetailsList);
                    } catch (Throwable throwable) {
                        LOGGER.error(throwable);
                    }
                }

                @Override
                public int priority() {
                    return PLUGIN_LISTENER_PRIORITY;
                }
            });
        }

        FrameworkMisc.EVENT_HANDLER.onPostServicesInitial();
    }

    /**
     * Shutdown Method for Bean Context
     */
    public void stop() {
        List<BeanDetails> detailsList = Lists.newArrayList(this.sortedBeans);
        Collections.reverse(detailsList);

        this.call(PreDestroy.class, detailsList);

        for (BeanDetails details : detailsList) {
            log("Bean " + details.getName() + " Disabled, due to framework being disabled.");

            details.onDisable();
            unregisterBean(details);
        }

        this.call(PostDestroy.class, detailsList);
    }

    public BeanDetails registerBean(BeanDetails beanDetails) {
        return this.registerBean(beanDetails, true);
    }

    public BeanDetails registerBean(BeanDetails beanDetails, boolean sort) {
        this.beanByType.put(beanDetails.getType(), beanDetails);
        this.beanByName.put(beanDetails.getName(), beanDetails);
        if (sort) {
            this.sortedBeans.add(beanDetails);
        }

        return beanDetails;
    }

    public Collection<BeanDetails> unregisterBean(Class<?> type) {
        return this.unregisterBean(this.getBeanDetails(type));
    }

    public Collection<BeanDetails> unregisterBean(String name) {
        return this.unregisterBean(this.getBeanByName(name));
    }

    // UNFINISHED, or finished? idk
    public Collection<BeanDetails> unregisterBean(@NonNull BeanDetails beanDetails) {
        this.beanByType.remove(beanDetails.getType());
        this.beanByName.remove(beanDetails.getName());

        this.lock.writeLock().lock();
        this.sortedBeans.remove(beanDetails);
        this.lock.writeLock().unlock();

        final ImmutableList.Builder<BeanDetails> builder = ImmutableList.builder();

        // Unregister Child Dependency
        for (String child : beanDetails.getChildren()) {
            BeanDetails childDetails = this.getBeanByName(child);

            builder.add(childDetails);
            builder.addAll(this.unregisterBean(childDetails));
        }

        // Remove Children from dependencies
        for (String dependency : beanDetails.getAllDependencies()) {
            BeanDetails dependDetails = this.getBeanByName(dependency);

            if (dependDetails != null) {
                dependDetails.removeChildren(beanDetails.getName());
            }
        }

        return builder.build();
    }

    public BeanDetails getBeanDetails(Class<?> type) {
        return this.beanByType.get(type);
    }

    public Object getBean(@NonNull Class<?> type) {
        BeanDetails details = this.getBeanDetails(type);
        if (details == null) {
            return null;
        }
        return details.getInstance();
    }

    public BeanDetails getBeanByName(String name) {
        return this.beanByName.get(name);
    }

    public boolean isRegisteredBeans(String... beans) {
        return Arrays.stream(beans)
                .map(this::getBeanByName)
                .noneMatch(dependencyDetails -> dependencyDetails == null || dependencyDetails.getInstance() == null);
    }

    public boolean isBean(Class<?> beanClass) {
        return this.beanByType.containsKey(beanClass);
    }

    public boolean isBean(Object bean) {
        return this.isBean(bean.getClass());
    }

    public Collection<BeanDetails> findDetailsBindWith(AbstractPlugin plugin) {
        return this.beanByType.values()
                .stream()
                .filter(beanDetails -> beanDetails.isBind() && beanDetails.getBindPlugin().equals(plugin))
                .collect(Collectors.toList());
    }

    /**
     * Injections
     */

    public void injectAutowired(Field field, Object instance) throws ReflectiveOperationException {
        Class<?> type = field.getType();
        boolean optional = false;
        if (type == Optional.class) {
            optional = true;
            final Type genericType = field.getGenericType();
            if (!(genericType instanceof ParameterizedType)) {
                LOGGER.error("The Autowired field " + field + " is optional but not parameterized!");
                return;
            }

            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            if (parameterizedType.getActualTypeArguments().length <= 0) {
                LOGGER.error("The Autowired field " + field + " is optional but has no parameters!");
                return;
            }

            type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        Object objectToInject = this.getBean(type);
        if (optional) {
            objectToInject = Optional.ofNullable(objectToInject);
        }

        if (objectToInject != null) {
            AccessUtil.setAccessible(field);
            Reflect.setField(instance, field, objectToInject);
        } else {
            LOGGER.error("The Autowired field " + field + " trying to wired with type " + type.getSimpleName() + " but couldn't find any matching Service! (or not being registered)");
        }
    }

    public void injectBeans(Object instance) {
        try {
            Collection<Field> fields = Reflect.getDeclaredFields(instance.getClass());

            for (Field field : fields) {
                int modifiers = field.getModifiers();
                Autowired annotation = field.getAnnotation(Autowired.class);

                if (annotation == null || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }

                this.injectAutowired(field, instance);
            }
        } catch (Throwable throwable) {
            LOGGER.error("Error while injecting beans for " + instance.getClass().getSimpleName(), throwable);
        }
    }

    /**
     * Registration
     */

    public ComponentBeanDetails registerComponent(Object instance, Class<?> type, ComponentHolder componentHolder) throws InvocationTargetException, IllegalAccessException {
        Component component = type.getAnnotation(Component.class);
        if (component == null) {
            throw new IllegalArgumentException("The type " + type.getName() + " doesn't have Component annotation!");
        }

        ServiceDependency serviceDependency = type.getAnnotation(ServiceDependency.class);
        if (serviceDependency != null) {
            for (String dependency : serviceDependency.dependencies()) {
                if (!this.isRegisteredBeans(dependency)) {
                    switch (serviceDependency.type().value()) {
                        case FORCE:
                            LOGGER.error("Couldn't find the dependency " + dependency + " for " + type.getSimpleName() + "!");
                        case SUB_DISABLE:
                            return null;
                        case SUB:
                            break;
                    }
                }
            }
        }

        String name = component.value();
        if (name.length() == 0) {
            name = instance.getClass().getName();
        }

        ComponentBeanDetails details = new ComponentBeanDetails(type, instance, name, componentHolder);
        if (!details.shouldInitialize()) {
            return null;
        }

        this.registerBean(details);
        this.attemptBindPlugin(details);

        try {
            details.call(PreInitialize.class);
        } catch (Throwable throwable) {
            LOGGER.error(throwable);
        }
        return details;
    }

    private void attemptBindPlugin(BeanDetails beanDetails) {
        if (PluginManager.isInitialized()) {
            AbstractPlugin plugin = PluginManager.INSTANCE.getPluginByClass(beanDetails.getType());

            if (plugin != null) {
                beanDetails.bindWith(plugin);

                log("Bean " + beanDetails.getName() + " is now bind with plugin " + plugin.getName());
            }
        }
    }

    public void scanClasses(String scanName, ClassLoader classLoader, Collection<String> classPaths, BeanDetails... included) throws Exception {
        log("Start scanning beans for %s with packages [%s]...", scanName, String.join(" ", classPaths));

        // Build the instance for Reflection Lookup
        ReflectLookup reflectLookup;
        try (SimpleTiming ignored = logTiming("Reflect Lookup building")) {
            reflectLookup = new ReflectLookup(Collections.singleton(classLoader), classPaths);
        }

        // Scanning through the JAR to see every Service Bean can be registered
        // Scanning methods that registers bean

        List<BeanDetails> beanDetailsList = new NonNullArrayList<>(Arrays.asList(included));
        try (SimpleTiming ignored = logTiming("Scanning Bean Method")) {
            for (Method method : reflectLookup.findAnnotatedStaticMethods(Bean.class)) {
                if (method.getReturnType() == void.class) {
                    new IllegalArgumentException("The Method " + method + " has annotated @Bean but no return type!").printStackTrace();
                }
                BeanParameterDetailsMethod detailsMethod = new BeanParameterDetailsMethod(method, this);
                final Object instance = detailsMethod.invoke(null, this);

                Bean bean = method.getAnnotation(Bean.class);
                if (bean == null) {
                    continue;
                }

                String name = bean.name();
                if (name.isEmpty()) {
                    name = instance.getClass().toString();
                }

                if (this.getBeanByName(name) == null) {

                    BeanDetails beanDetails = new DependenciesBeanDetails(instance.getClass(), instance, name, Arrays.stream(detailsMethod.getParameters()).map(type -> this.getBeanDetails(type.getType())).filter(Objects::nonNull).map(BeanDetails::getName).toArray(String[]::new));

                    log("Found " + name + " with type " + instance.getClass().getSimpleName() + ", Registering it as bean...");

                    this.attemptBindPlugin(beanDetails);
                    this.registerBean(beanDetails, false);

                    beanDetailsList.add(beanDetails);
                } else {
                    new ServiceAlreadyExistsException(name).printStackTrace();
                }
            }
        }
        try (SimpleTiming ignored = logTiming("Scanning Beans")) {

            for (Class<?> type : reflectLookup.findAnnotatedClasses(Service.class)) {

                Service service = type.getAnnotation(Service.class);
                Preconditions.checkNotNull(service, "The type " + type.getName() + " doesn't have @Service annotation!");

                String name = service.name();

                if (this.getBeanByName(name) == null) {
                    ServiceBeanDetails beanDetails = new ServiceBeanDetails(type, name, service.dependencies());

                    log("Found " + name + " with type " + type.getSimpleName() + ", Registering it as bean...");

                    this.attemptBindPlugin(beanDetails);
                    this.registerBean(beanDetails, false);

                    beanDetailsList.add(beanDetails);
                } else {
                    new ServiceAlreadyExistsException(name).printStackTrace();
                }
            }
        }

        // Load Beans in Dependency Tree Order
        try (SimpleTiming ignored = logTiming("Initializing Beans")) {
            beanDetailsList = this.loadInOrder(beanDetailsList);
        } catch (Throwable throwable) {
            LOGGER.error("An error occurs while handling loadInOrder()", throwable);
        }

        // Unregistering Beans that returns false in shouldInitialize
        try (SimpleTiming ignored = logTiming("Unregistering Disabled Beans")) {
            this.sortedBeans.addAll(beanDetailsList);

            for (BeanDetails beanDetails : ImmutableList.copyOf(beanDetailsList)) {
                if (!beanDetailsList.contains(beanDetails)) {
                    continue;
                }
                try {
                    if (!beanDetails.shouldInitialize()) {
                        log("Unregistering " + beanDetails.getName() + " due to it cancelled to register");

                        beanDetailsList.remove(beanDetails);
                        for (BeanDetails details : this.unregisterBean(beanDetails)) {
                            log("Unregistering " + details.getName() + " due to it dependency unregistered");

                            beanDetailsList.remove(details);
                        }
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    LOGGER.error(e);
                    this.unregisterBean(beanDetails);
                }
            }
        }

        // Call @PreInitialize methods for bean
        try (SimpleTiming ignored = logTiming("Call @PreInitialize")) {
            this.call(PreInitialize.class, beanDetailsList);
        }

        // Scan Components
        try (SimpleTiming ignored = logTiming("Scanning Components")) {
            beanDetailsList.addAll(ComponentRegistry.scanComponents(this, reflectLookup));
        }

        // Inject @Autowired fields for beans
        try (SimpleTiming ignored = logTiming("Injecting Beans")) {
            beanDetailsList.forEach(beanDetails -> {
                Object instance = beanDetails.getInstance();
                if (instance != null) {
                    this.injectBeans(instance);
                }
            });
        }

        // Inject @Autowired static fields
        try (SimpleTiming ignored = logTiming("Injecting Static Autowired Fields")) {
            for (Field field : reflectLookup.findAnnotatedStaticFields(Autowired.class)) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                this.injectAutowired(field, null);
            }
        }

        // Call onEnable() for Components
        try (SimpleTiming ignored = logTiming("Call onEnable() for Components")) {
            beanDetailsList.forEach(BeanDetails::onEnable);
        }

        // Call @PostInitialize
        try (SimpleTiming ignored = logTiming("Call @PostInitialize")) {
            this.call(PostInitialize.class, beanDetailsList);
        }

    }

    public void call(Class<? extends Annotation> annotation, Collection<BeanDetails> beanDetailsList) {
        for (BeanDetails beanDetails : beanDetailsList) {
            try {
                beanDetails.call(annotation);
            } catch (Throwable throwable) {
                LOGGER.error(throwable);
            }
        }
    }

    private List<BeanDetails> loadInOrder(List<BeanDetails> beanDetailsList) {
        beanDetailsList.sort(new BeanDetailsComparator());
        Map<String, BeanDetails> unloaded = new HashMap<>();
        for (BeanDetails beanDetails : beanDetailsList) {
            unloaded.put(beanDetails.getName(), beanDetails);

            if (beanDetails instanceof ServiceBeanDetails) {
                ((ServiceBeanDetails) beanDetails).setupConstruction(this);
            }
        }

        // Remove Services without valid dependency
        Iterator<Map.Entry<String, BeanDetails>> removeIterator = unloaded.entrySet().iterator();
        while (removeIterator.hasNext()) {
            Map.Entry<String, BeanDetails> entry = removeIterator.next();
            BeanDetails beanDetails = entry.getValue();

            if (!beanDetails.hasDependencies()) {
                continue;
            }

            for (Map.Entry<ServiceDependencyType, List<String>> allDependency : beanDetails.getDependencyEntries()) {
                final ServiceDependencyType type = allDependency.getKey();

                search:
                for (String dependency : allDependency.getValue()) {
                    BeanDetails dependencyDetails = this.getBeanByName(dependency);

                    if (dependencyDetails == null) {
                        switch (type) {
                            case FORCE:
                                LOGGER.error("Couldn't find the dependency " + dependency + " for " + beanDetails.getName() + "!");
                                removeIterator.remove();
                                break search;
                            case SUB_DISABLE:
                                removeIterator.remove();
                                break search;
                            case SUB:
                                break;
                        }
                        // Prevent dependency each other
                    } else {
                        if (dependencyDetails.hasDependencies()
                                && dependencyDetails.getAllDependencies().contains(beanDetails.getName())) {
                            LOGGER.error("Target " + beanDetails.getName() + " and " + dependency + " depend to each other!");
                            removeIterator.remove();

                            unloaded.remove(dependency);
                            break;
                        }

                        dependencyDetails.addChildren(beanDetails.getName());
                    }
                }
            }
        }

        // Continually loop until all dependency found and loaded
        List<BeanDetails> sorted = new NonNullArrayList<>();

        while (!unloaded.isEmpty()) {
            Iterator<Map.Entry<String, BeanDetails>> iterator = unloaded.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, BeanDetails> entry = iterator.next();
                BeanDetails beanDetails = entry.getValue();
                boolean missingDependencies = false;

                for (Map.Entry<ServiceDependencyType, List<String>> dependencyEntry : beanDetails.getDependencyEntries()) {
                    final ServiceDependencyType type = dependencyEntry.getKey();
                    for (String dependency : dependencyEntry.getValue()) {
                        BeanDetails dependencyDetails = this.getBeanByName(dependency);
                        if (dependencyDetails != null && dependencyDetails.getInstance() != null) {
                            continue;
                        }

                        if (type == ServiceDependencyType.SUB && !unloaded.containsKey(dependency)) {
                            continue;
                        }

                        missingDependencies = true;
                    }
                }

                if (!missingDependencies) {
                    if (beanDetails instanceof ServiceBeanDetails) {
                        ((ServiceBeanDetails) beanDetails).build(this);
                    }

                    sorted.add(beanDetails);
                    iterator.remove();
                }
            }
        }

        return sorted;
    }

    public List<String> findClassPaths(Class<?> plugin) {
        ClasspathScan annotation = plugin.getAnnotation(ClasspathScan.class);

        if (annotation != null) {
            return Lists.newArrayList(annotation.value());
        }

        return Collections.emptyList();
    }

}

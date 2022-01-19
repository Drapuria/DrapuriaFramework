package net.drapuria.framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import lombok.experimental.UtilityClass;
import net.drapuria.framework.events.IEventHandler;
import net.drapuria.framework.jackson.libraries.Library;
import net.drapuria.framework.jackson.libraries.LibraryHandler;
import net.drapuria.framework.jackson.libraries.annotation.MavenDependency;
import net.drapuria.framework.beans.BeanContext;
import net.drapuria.framework.task.ITaskScheduler;
import net.drapuria.framework.util.ClasspathScanner;
import net.drapuria.framework.util.MethodAnnotationScanner;
import net.drapuria.framework.util.Stacktrace;
import net.drapuria.framework.util.TypeAnnotationScanner;
import net.drapuria.framework.util.terminable.Terminable;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@UtilityClass
public final class DrapuriaCommon {

    public final String METADATA_PREFIX = "Drapuria_";

    private final Set<Library> GLOBAL_LIBRARIES_INDEPENDENT_CLASSLOADER = ImmutableSet.of(
            Library.H2_DRIVER
    );

    private final Set<Library> GLOBAL_LIBRARIES = ImmutableSet.of(
            // SQL
            Library.MARIADB_DRIVER,
            Library.HIKARI,
            Library.MYSQL_DRIVER,
            Library.POSTGRESQL_DRIVER,

            // MONGO
            Library.MONGO_DB_SYNC,
            Library.MONGO_DB_CORE,

            // CLASSPATH SCANNER
            Library.FAST_CLASSPATH_SCANNER,

            Library.BSON,
            Library.CAFFEINE,
            Library.GSON,

            // Spring
            Library.SPRING_CORE,
            Library.SPRING_EL,

            // Jackson
            Library.JACKSON_CORE,
            Library.JACKSON_DATABIND,
            Library.JACKSON_ANNOTATIONS

    );

    public DrapuriaPlatform PLATFORM;
    public BeanContext BEAN_CONTEXT;

    public LibraryHandler LIBRARY_HANDLER;


    public IEventHandler EVENT_HANDLER;
    public ITaskScheduler TASK_SCHEDULER;

    private boolean LIBRARIES_INITIALIZED, BRIDGE_INITIALIZED;

    public ExecutorService executorService;

    private final List<Terminable> TERMINATES = new ArrayList<>();


    public void init() {
        DrapuriaCommon.loadLibraries();
        DrapuriaCommon.BEAN_CONTEXT = new BeanContext();
        DrapuriaCommon.executorService = Executors.newCachedThreadPool();
        DrapuriaCommon.BEAN_CONTEXT.init();
    }

    public void loadLibraries() {

        if (DrapuriaCommon.LIBRARIES_INITIALIZED) {
            return;
        }
        DrapuriaCommon.LIBRARIES_INITIALIZED = true;

        getLogger().info("Loading Libraries");

        DrapuriaCommon.LIBRARY_HANDLER = new LibraryHandler();
        DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, GLOBAL_LIBRARIES);

        DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(false, GLOBAL_LIBRARIES_INDEPENDENT_CLASSLOADER);

        try {
            Class.forName("com.google.common.collect.ImmutableList");
        } catch (ClassNotFoundException ex) {
            // Below 1.8
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, Library.GUAVA);
        }

        try {
            Class.forName("it.unimi.dsi.fastutil.Arrays");
        } catch (ClassNotFoundException ex) {
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, Library.FAST_UTIL);
        }

        try {
            Class.forName("org.yaml");
        } catch (ClassNotFoundException ex) {
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, Library.YAML);
        }
        DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, Library.REDISSON);


        loadAnnotatedDependencies(ClasspathScanner.getCodeSourceOf(DrapuriaCommon.LIBRARY_HANDLER), "net.drapuria");
        FrameworkMisc.LIBRARY_HANDLER = DrapuriaCommon.LIBRARY_HANDLER;
    }

    public void loadAnnotatedDependencies(CodeSource codeSource, String packageName) {
        getLogger().info("[Drapuria] Scanning for annotated maven dependencies in " + packageName);
        final TypeAnnotationScanner typeAnnotationScanner = new TypeAnnotationScanner(
                codeSource,
                packageName, MavenDependency.class);
        for (Class<?> annotatedClass : typeAnnotationScanner.getResult()) {
            for (MavenDependency mavenDependency : annotatedClass.getAnnotationsByType(MavenDependency.class)) {
                if (DrapuriaCommon.LIBRARY_HANDLER.getLoaded().keySet()
                        .stream().noneMatch(library ->
                                library.getName().equalsIgnoreCase(mavenDependency.artifactId())
                                        && library.getVersion().equalsIgnoreCase(mavenDependency.version())
                                        && library.getGroupId().equalsIgnoreCase(mavenDependency.groupId()))) {
                    DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, new Library(
                            mavenDependency.groupId(),
                            mavenDependency.artifactId(),
                            mavenDependency.versionPackage().isEmpty() ? mavenDependency.version() : mavenDependency.versionPackage(),
                            mavenDependency.version(),
                            null,
                            mavenDependency.repo().url()
                    ));
                }
            }
        }
        getLogger().info("scanned anotated maven dependencies");
    }

    public Logger getLogger() {
        return DrapuriaCommon.PLATFORM.getLogger();
    }

    public <T> T getBean(Class<T> type) {
        return (T) BEAN_CONTEXT.getBean(type);
    }

    public void injectBean(Object instance) {
        BEAN_CONTEXT.injectBeans(instance);
    }

    public void shutdown() throws Throwable {
        synchronized (DrapuriaCommon.TERMINATES) {
            for (Terminable terminable : DrapuriaCommon.TERMINATES) {
                terminable.close();
            }
        }
        executorService.shutdown();
        DrapuriaCommon.BEAN_CONTEXT.stop();
        FrameworkMisc.close();
    }


    public void addTerminable(Terminable terminable) {
        synchronized (DrapuriaCommon.TERMINATES) {
            DrapuriaCommon.TERMINATES.add(terminable);
        }
    }

    public Builder builder() {
        if (DrapuriaCommon.BRIDGE_INITIALIZED) {
            throw new IllegalStateException("Already builded!");
        }

        DrapuriaCommon.BRIDGE_INITIALIZED = true;
        return new Builder();
    }

    public class Builder {

        private DrapuriaPlatform platform;
        private IEventHandler eventHandler;
        private ITaskScheduler taskScheduler;
        private ObjectMapper mapper;

        public Builder platform(DrapuriaPlatform bridge) {
            this.platform = bridge;
            return this;
        }


        public Builder eventHandler(IEventHandler eventHandler) {
            this.eventHandler = eventHandler;
            return this;
        }

        public Builder taskScheduler(ITaskScheduler taskScheduler) {
            this.taskScheduler = taskScheduler;
            return this;
        }


        public void init() {
            if (this.platform != null) {
                DrapuriaCommon.PLATFORM = this.platform;
                FrameworkMisc.PLATFORM = this.platform;
            }
            if (this.eventHandler != null) {
                DrapuriaCommon.EVENT_HANDLER = this.eventHandler;
                FrameworkMisc.EVENT_HANDLER = this.eventHandler;
            }
            if (this.taskScheduler != null) {
                DrapuriaCommon.TASK_SCHEDULER = this.taskScheduler;
                FrameworkMisc.TASK_SCHEDULER = this.taskScheduler;
            }
            DrapuriaCommon.init();
        }
    }
}

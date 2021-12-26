package net.drapuria.framework.module;

import lombok.Getter;
import net.drapuria.framework.module.classloader.ModuleClassLoader;
import net.drapuria.framework.module.parent.ModuleParent;

import java.io.File;

@Getter
public abstract class JavaModule implements Module {

    private ModuleParent<?> moduleParent;
    private String name;
    private String author;
    private String version;
    private String[] description;
    private ModuleClassLoader classLoader;
    private File file;
    private File dataFolder;

    private boolean isEnabled = false;

    public JavaModule() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof ModuleClassLoader)) {
            throw new IllegalStateException("Drapuruia Module requires " + ModuleClassLoader.class.getName());
        }
    }

    public final void init(File file, String name, String author, String version, String[] description, ModuleClassLoader classLoader) {
        this.file = file;
        this.name = name;
        this.author = author;
        this.version = version;
        this.description = description;
        this.classLoader = classLoader;
        this.dataFolder = new File(moduleParent.getDataFolder() + File.separator + "modules" + File.separator + name);
    }

    @Override
    public File getDataFolder() {
        return this.dataFolder;
    }

    public ModuleParent<?> getModuleParent() {
        return moduleParent;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
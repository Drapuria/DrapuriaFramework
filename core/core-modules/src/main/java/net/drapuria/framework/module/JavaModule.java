package net.drapuria.framework.module;

import net.drapuria.framework.module.classloader.ModuleClassLoader;

import java.io.File;

public abstract class JavaModule implements Module {

    private String name;
    private String author;
    private String version;
    private String[] description;
    private File file;

    public JavaModule() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof ModuleClassLoader)) {
            throw new IllegalStateException("Drapuruia Module requires " + ModuleClassLoader.class.getName());
        }
        ((ModuleClassLoader) classLoader).initialize(this);    }

    final void init(File file, String name, String author, String version, String[] description, ModuleClassLoader classLoader) {

    }

}

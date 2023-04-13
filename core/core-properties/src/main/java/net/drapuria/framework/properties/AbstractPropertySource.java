package net.drapuria.framework.properties;

import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.plugin.AbstractPlugin;

import java.io.File;

public abstract class AbstractPropertySource implements PropertySource {

    private final File file;
    protected AbstractPropertySource(AbstractPlugin plugin) {
       this(new File(plugin.getDataFolder(), plugin.getName() + ".yml"));
    }

    protected AbstractPropertySource(DrapuriaPlatform platform) {
        this(new File(platform.getDataFolder(), "drapuria.yml"));
    }

    protected AbstractPropertySource(final File file) {
        this.file = file;
        this.setup();
    }

    @Override
    public File getFile() {
        return this.file;
    }

    protected abstract void setup();

}
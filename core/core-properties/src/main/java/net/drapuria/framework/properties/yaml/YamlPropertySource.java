package net.drapuria.framework.properties.yaml;

import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.properties.AbstractPropertySource;
import net.drapuria.framework.properties.PropertyAdapter;

import java.io.File;

public class YamlPropertySource extends AbstractPropertySource {

    private YamlPropertyAdapter adapter;

    public YamlPropertySource(AbstractPlugin plugin) {
        super(plugin);
    }

    public YamlPropertySource(DrapuriaPlatform platform) {
        super(platform);
    }

    public YamlPropertySource(File file) {
        super(file);
    }

    @Override
    protected void setup() {
        this.adapter = new YamlPropertyAdapter(super.getFile());
    }

    @Override
    public PropertyAdapter getAdapter() {
        return this.adapter;
    }
}

package net.drapuria.framework.properties.properties;

import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.properties.AbstractPropertySource;
import net.drapuria.framework.properties.PropertyAdapter;

public class PropertiesPropertySource extends AbstractPropertySource {
    protected PropertiesPropertySource(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void setup() {

    }

    @Override
    public PropertyAdapter getAdapter() {
        return null;
    }
}

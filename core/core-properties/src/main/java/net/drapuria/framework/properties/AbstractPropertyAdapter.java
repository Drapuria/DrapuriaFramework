package net.drapuria.framework.properties;

import net.drapuria.framework.properties.yaml.YamlPropertyAdapter;

import java.io.File;

public abstract class AbstractPropertyAdapter implements PropertyAdapter {

    protected final File file;

    protected AbstractPropertyAdapter(final File file) {
        this.file = file;
        this.setupAdapter();
    }

    protected abstract void setupAdapter();

}

package net.drapuria.framework.properties.yaml;

import net.drapuria.framework.configuration.Configuration;
import net.drapuria.framework.configuration.yaml.SimpleYamlConfiguration;
import net.drapuria.framework.properties.AbstractPropertyAdapter;

import java.io.File;
import java.io.IOException;

public class YamlPropertyAdapter extends AbstractPropertyAdapter {

    private final SimpleYamlConfiguration yamlConfiguration = new SimpleYamlConfiguration();
    private Configuration configuration;

    protected YamlPropertyAdapter(File file) {
        super(file);
    }


    @Override
    public <T> T readProperty(String property) {
        return (T) configuration.get(property);
    }

    @Override
    public <T> T readProperty(String property, T definition) {
        return configuration.get(property, definition);
    }

    @Override
    public void setProperty(String property, Object value) {
        configuration.set(property, value);
        try {
            this.yamlConfiguration.save(configuration, super.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void setupAdapter() {
        try {
            if (!super.file.exists())
                super.file.createNewFile();
            this.configuration = this.yamlConfiguration.load(super.file);
        } catch (IOException e) {
                throw new RuntimeException(e);
        }

    }
}
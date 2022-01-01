package net.drapuria.framework.bukkit.util.option.object;

import net.drapuria.framework.bukkit.util.option.OptionContext;
import net.drapuria.framework.services.BeanContext;
import net.drapuria.framework.services.SerializerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class OptionBasedObject {

    private final Map<String, OptionContext<?, ?, ?>> options = new HashMap<>();

    public OptionBasedObject() {
        registerOptions();
    }

    public abstract void registerOptions();

    public void addOption(OptionContext<?, ?, ?> optionContext) {
        this.options.put(optionContext.getOptionName(), optionContext);
    }

    public Map<String, OptionContext<?, ?, ?>> getOptions() {
        return options;
    }

    public OptionContext<?, ?, ?> getOption(String optionName) {
        return this.options.getOrDefault(optionName, null);
    }

    public Map<String, String> serializeOptions() {
        final Map<String, String> serializedOptions = new HashMap<>();
        for (OptionContext<?, ?, ?> value : options.values()) {
            serializedOptions.put(value.getOptionName(), value.serializeData());
        }
        return serializedOptions;
    }

    public void deserializeOptions(final Map<String, String> serializedOptions) {
        for (Map.Entry<String, String> entry : serializedOptions.entrySet()) {
            OptionContext<?, ?, ?> optionContext = getOption(entry.getKey());
            if (optionContext != null)
                optionContext.deserializeAndSet(entry.getValue());
        }
    }
}

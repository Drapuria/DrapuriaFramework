package net.drapuria.framework.bukkit.util.option.object;

import net.drapuria.framework.bukkit.util.option.OptionContext;

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
}

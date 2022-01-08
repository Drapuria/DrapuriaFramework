package net.drapuria.framework.discord;

import net.drapuria.framework.beans.annotation.PostDestroy;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.drapuria.framework.discord.bot.DiscordBotFactory;
import net.drapuria.framework.discord.component.DiscordBotConfigurationComponentHolder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.EncodingUtil;

import java.util.HashMap;
import java.util.Map;

@Service(name = "discord", dependencies = {"sql", "FrameworkDatabaseService"})
public class DiscordService {

    private final Map<Class<?>, DiscordBotFactory<?>> factories = new HashMap<>();

    public static DiscordService getService;

    private Class<?> defaultConfiguration;

    @PreInitialize
    public void preInit() {
        getService = this;
        ComponentRegistry.registerComponentHolder(new DiscordBotConfigurationComponentHolder(this));
    }


    public Class<?> getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public void setDefaultConfiguration(Class<?> defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }

    public void addFactory(Class<?> configuration, DiscordBotFactory<?> factory) {
        if (factories.containsKey(configuration)) {
            return;
        }
        factories.put(configuration, factory);
    }

    @PostDestroy
    public void stop() {
        factories.values().forEach(DiscordBotFactory::shutdown);
    }

    public DiscordBotFactory<?> factory(Class<?> config, Class<DiscordBotFactory<?>> type) {
        if (!factories.containsKey(config)) {
            if (this.defaultConfiguration == null)
                return null;
            return this.factories.get(this.defaultConfiguration);
        }
        return this.factories.get(config);
    }
}

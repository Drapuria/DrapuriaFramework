package net.drapuria.framework.discord.component;

import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.discord.DiscordService;
import net.drapuria.framework.discord.bot.DiscordBotFactory;
import net.drapuria.framework.discord.configuration.AbstractDiscordBotConfiguration;

public class DiscordBotConfigurationComponentHolder extends ComponentHolder {

    private final DiscordService service;

    public DiscordBotConfigurationComponentHolder(DiscordService service) {
        this.service = service;
    }

    @Override
    public Class<?>[] type() {
        return new Class[]{AbstractDiscordBotConfiguration.class};
    }

    @Override
    public void onEnable(Object instance) {
        AbstractDiscordBotConfiguration<?> configuration = (AbstractDiscordBotConfiguration<?>) instance;
        if (!configuration.shouldActivate())
            return;
        if (service.getDefaultConfiguration() == null)
            service.setDefaultConfiguration(configuration.getClass());
        DiscordBotFactory<?> factory = configuration.factory();
        if (configuration.shouldActivate())
            factory.init();
        service.addFactory(configuration.getClass(), factory);
    }
}

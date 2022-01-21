/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.configuration;

import net.drapuria.framework.discord.bot.JDAFactory;

public abstract class JDADiscordBotConfiguration extends AbstractDiscordBotConfiguration<JDAFactory> {

    private JDAFactory factory;

    @Override
    public JDAFactory factory() {
        if (factory == null)
            this.factory = new JDAFactory(this);
        return this.factory;
    }
}

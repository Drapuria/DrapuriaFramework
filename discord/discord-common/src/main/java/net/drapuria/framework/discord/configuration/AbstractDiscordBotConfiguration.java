/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.configuration;

import net.drapuria.framework.discord.bot.DiscordBotFactory;

public abstract class AbstractDiscordBotConfiguration<F extends DiscordBotFactory<?>> {

    public boolean shouldActivate() {
        return false;
    }

    public abstract F factory();

    public abstract String token();


}

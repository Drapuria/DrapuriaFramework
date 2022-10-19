/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.bot;

public abstract class DiscordBotFactory<B> {

    public DiscordBotFactory<B> init() {
        setupLibraries();
        return this;
    }

    public abstract B create();

    public abstract void shutdown();

    public abstract B get();

    public abstract DiscordBotFactory<B> setupLibraries();
}

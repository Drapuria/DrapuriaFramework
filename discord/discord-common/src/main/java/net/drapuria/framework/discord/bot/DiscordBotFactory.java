package net.drapuria.framework.discord.bot;

public abstract class DiscordBotFactory<B> {

    public B init() {
        setupLibraries();
        return create();
    }

    public abstract B create();

    public abstract void shutdown();

    public abstract B get();

    public abstract void setupLibraries();
}

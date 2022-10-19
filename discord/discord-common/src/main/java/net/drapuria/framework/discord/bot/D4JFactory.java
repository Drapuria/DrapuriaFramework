/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.bot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.discord.configuration.AbstractDiscordBotConfiguration;
import net.drapuria.framework.libraries.Library;

import java.util.concurrent.atomic.AtomicReference;

public class D4JFactory extends DiscordBotFactory<GatewayDiscordClient> {

    private final AbstractDiscordBotConfiguration<?> configuration;

    private GatewayDiscordClient client;

    public D4JFactory(AbstractDiscordBotConfiguration<?> configuration) {
        this.configuration = configuration;
    }

    @Override
    public GatewayDiscordClient create() {
        DiscordClient client = DiscordClient.create(configuration.token());

        AtomicReference<GatewayDiscordClient> atomicReference = new AtomicReference<>();
      //  client.gateway().
        //return client;
        return null;
    }

    @Override
    public void shutdown() {
        if (this.client != null) {
        //    this.client.login
        }
    }

    @Override
    public GatewayDiscordClient get() {
        return client;
    }

    public D4JFactory setupLibraries() {
        try {
            Class.forName("discord4j.core.DiscordClient");
        } catch (Exception ignored) {
            Library library = new Library("com.discord4j", "discord4j-core", "3.2.1", null);
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, library);
        }
        return this;
    }
}

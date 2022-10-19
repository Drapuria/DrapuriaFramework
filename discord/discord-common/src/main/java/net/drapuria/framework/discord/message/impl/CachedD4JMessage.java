/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.message.impl;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import net.drapuria.framework.discord.bot.D4JFactory;
import net.drapuria.framework.discord.bot.JDAFactory;
import net.drapuria.framework.discord.message.CachedMessage;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CachedD4JMessage extends CachedMessage<TextChannel, Mono<Message>, Message, Mono<Void>> {

    public static D4JFactory factory;

    private final Snowflake channelSnowflake;
    private final Snowflake messageSnowflake;

    public CachedD4JMessage(Message message) {
        super(message.getChannelId().asLong(), message.getId().asLong(), message.getContent());
        this.channelSnowflake = Snowflake.of(super.getChannelId());
        this.messageSnowflake = Snowflake.of(super.getMessageId());
    }

    @Override
    public boolean equalsMessage(Message message) {
        return this.channelId == message.getChannelId().asLong() && this.messageId == message.getId().asLong();
    }

    @Override
    public TextChannel getChannel() {
        return (TextChannel) factory.get().getChannelById(channelSnowflake).as(Mono::block);
    }

    @Override
    public Mono<Message> getMessage() {
        return factory.get().getMessageById(channelSnowflake, messageSnowflake).cache();
    }

    @Override
    public Mono<Void> delete() {
        return getMessage().block().delete();
    }
}

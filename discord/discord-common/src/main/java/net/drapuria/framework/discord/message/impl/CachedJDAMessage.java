/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.message.impl;

import net.drapuria.framework.discord.bot.JDAFactory;
import net.drapuria.framework.discord.message.CachedMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CachedJDAMessage extends CachedMessage<TextChannel, RestAction<?>, Message, CompletableFuture<Void>> {

    public static JDAFactory factory;


    public CachedJDAMessage(final Message message) {
        super(message.getIdLong(), message.getChannel().getIdLong(), message.getContentRaw());
    }


    @Nullable
    public RestAction<Message> getMessage() {
        TextChannel textChannel = this.getChannel();
        if (textChannel == null)
            return null;
        return textChannel.retrieveMessageById(messageId);
    }


    @Override
    public boolean equalsMessage(Message message) {
        return this.channelId == message.getChannel().getIdLong() && this.messageId == message.getIdLong();
    }

    @Nullable
    public TextChannel getChannel() {
        return factory.get().getTextChannelById(this.channelId);
    }

    public CompletableFuture<Void> delete() {
        TextChannel textChannel = this.getChannel();
        if (textChannel == null)
            return CompletableFuture.completedFuture(null);
        return textChannel.deleteMessageById(this.messageId).submit();
    }
}

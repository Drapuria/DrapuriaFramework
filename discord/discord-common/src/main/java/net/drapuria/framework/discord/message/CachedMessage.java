/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.CompletableFuture;


@AllArgsConstructor
@Data
public abstract class CachedMessage<TC, R, M, D> {

    protected final long messageId;
    protected final long channelId;


    @SuppressWarnings({"unchecked", "EqualsWhichDoesntCheckParameterClass"})
    public boolean equals(Object message) {
        try {
            M ignored = (M) message;
        } catch (Exception ignored) {
            return false;
        }
        return equalsMessage((M) message);
    }

    public abstract boolean equalsMessage(M message);

    public abstract TC getChannel();

    public abstract R getMessage();

    public abstract D delete();

}

/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.redis.message.subscription;

import org.redisson.api.RTopic;

import java.util.function.Consumer;

public class RedisPubSub<T> {

    private final String name;
    private final RTopic topic;
    private final Class<T> type;

    public RedisPubSub(String name, RTopic topic, Class<T> type) {
        this.name = name;
        this.topic = topic;
        this.type = type;
    }

    public void publish(Object payload) {
        this.topic.publishAsync(payload);
    }

    public void subscribe(Consumer<T> subscription) {
        this.topic.addListenerAsync(this.type, (channel, message) -> subscription.accept(message));
    }

    public void disable() {
        this.topic.removeAllListeners();
    }

}

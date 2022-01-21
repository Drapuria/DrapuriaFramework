/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.redis.message.subscription;

public interface RedisSubscription {

    void onReceive(Object message);

}

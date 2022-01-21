/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util;

import java.util.function.LongConsumer;

public class SimpleTiming implements AutoCloseable {

    public static SimpleTiming create(LongConsumer consumer) {
        return new SimpleTiming(consumer);
    }

    private final LongConsumer consumer;
    private final long startMillis;

    private SimpleTiming(LongConsumer consumer) {
        this.consumer = consumer;
        this.startMillis = System.currentTimeMillis();
    }

    @Override
    public void close() throws Exception {
        this.consumer.accept(System.currentTimeMillis() - this.startMillis);
    }
}

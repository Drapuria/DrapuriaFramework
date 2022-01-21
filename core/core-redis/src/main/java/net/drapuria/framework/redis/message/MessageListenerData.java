/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.redis.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

@AllArgsConstructor
@Data
public class MessageListenerData {

    private final Object instance;
    private final Method method;
    private final Class<?> messageClass;

}

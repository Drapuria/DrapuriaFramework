package net.drapuria.framework.redis.message;

import net.drapuria.framework.redis.RedisService;
import net.drapuria.framework.redis.message.annotation.Message;
import net.drapuria.framework.redis.message.annotation.MessageHandler;
import net.drapuria.framework.redis.message.subscription.RedisPubSub;
import net.drapuria.framework.beans.annotation.Autowired;
import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.beans.component.ComponentRegistry;
import net.drapuria.framework.util.AccessUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(name = "redisMessageService")
public class MessageService {

    @Autowired
    public static MessageService getService;

    private RedisPubSub<Object> redisPubSub;
    private String channel;

    private final Map<Class<?>, List<MessageListenerData>> messageListeners = new HashMap<>();

    @Autowired
    private RedisService redisService;

    @PreInitialize
    public void registerComponent() {
        ComponentRegistry.registerComponentHolder(new ComponentHolder() {
            @Override
            public Class<?>[] type() {
                return new Class[]{MessageListener.class};
            }

            @Override
            public Object newInstance(Class<?> type) {
                Object instance = super.newInstance(type);
                registerListener((MessageListener) instance);
                return instance;
            }
        });
    }

    @PostInitialize
    public void start() {
        this.channel = "drapuria-redis";
        this.redisPubSub = new RedisPubSub<>(this.channel,
                this.redisService.getClient().getTopic(this.channel), Object.class);
        this.redisPubSub.subscribe(message -> {
            List<MessageListenerData> listeners = this.messageListeners.get(message.getClass());
            if (listeners == null) {
                return;
            }
            for (MessageListenerData data : listeners) {
                try {
                    data.getMethod().invoke(data.getInstance(), message);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    public void sendMessage(Object message) {
        try {
            if (message == null) {
                throw new IllegalStateException("The Message given a null serialized data!");
            }

            Class<?> type = message.getClass();
            if (!this.isAnnotated(type)) {
                throw new IllegalArgumentException("The Message " + message.getClass() + " does not have @Message Annotation!");
            }

            this.redisPubSub.publish(message);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public boolean isAnnotated(Class<?> messageClass) {
        while (messageClass != null && messageClass != Object.class) {
            if (messageClass.getAnnotation(Message.class) != null) {
                return true;
            }

            messageClass = messageClass.getSuperclass();
        }

        return false;
    }

    public void registerListener(MessageListener messageListener) {
        Method[] methods = messageListener.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.getDeclaredAnnotation(MessageHandler.class) == null) {
                continue;
            }
            if (method.getParameters().length != 1) {
                continue;
            }
            Class<?> messageClass = method.getParameterTypes()[0];

            List<MessageListenerData> listeners;
            if (this.messageListeners.containsKey(messageClass)) {
                listeners = this.messageListeners.get(messageClass);
            } else {
                listeners = new ArrayList<>();
                this.messageListeners.put(messageClass, listeners);
            }

            try {
                AccessUtil.setAccessible(method);
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(e);
            }
            listeners.add(new MessageListenerData(messageListener, method, messageClass));
        }
    }

}

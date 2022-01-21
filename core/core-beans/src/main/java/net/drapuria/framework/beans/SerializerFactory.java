/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.beans;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.*;
import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.beans.component.ComponentRegistry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service(name = "serializer")
@ServiceDependency(dependencies = "jackson")
@Getter
public class SerializerFactory {

    private Map<Class<?>, ObjectSerializer<?, ?>> serializers;

    @Autowired
    private JacksonService jacksonService;
    private Queue<ObjectSerializer<?, ?>> serializerQueue;

    @PreInitialize
    public void preInit() {
        this.serializerQueue = new ConcurrentLinkedQueue<>();
        this.serializers = new ConcurrentHashMap<>();

        ComponentRegistry.registerComponentHolder(new ComponentHolder() {
            @Override
            public Class<?>[] type() {
                return new Class[] { ObjectSerializer.class };
            }

            @Override
            public Object newInstance(Class<?> type) {
                ObjectSerializer<?, ?> serializer = (ObjectSerializer<?, ?>) super.newInstance(type);

                if (serializers.containsKey(serializer.inputClass())) {
                    throw new IllegalArgumentException("The Serializer for " + serializer.inputClass().getName() + " already exists!");
                }

                if (jacksonService == null) {
                    serializerQueue.add(serializer);
                } else {
                    jacksonService.registerJacksonConfigure(new SerializerJacksonConfigure(serializer));
                }
                serializers.put(serializer.inputClass(), serializer);
                return serializer;
            }
        });
    }

    @PostInitialize
    public void postInit() {
        ObjectSerializer<?, ?> serializer;
        while ((serializer = serializerQueue.poll()) != null) {
            jacksonService.registerJacksonConfigure(new SerializerJacksonConfigure(serializer));
        }

        serializerQueue = null;
    }

    @Nullable
    public ObjectSerializer<?, ?> findSerializer(Class<?> type) {
        return this.serializers.getOrDefault(type, null);
    }

    public static class JacksonSerailizer extends StdSerializer {

        private final ObjectSerializer serializer;

        public JacksonSerailizer(ObjectSerializer serializer) {
            super(serializer.inputClass());

            this.serializer = serializer;
        }

        @Override
        public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
           jsonGenerator.writeObject(serializer.serialize(o));
        }
    }

    public static class JacksonDeserailizer extends StdDeserializer {

        private final ObjectSerializer serializer;

        public JacksonDeserailizer(ObjectSerializer serializer) {
            super(serializer.inputClass());

            this.serializer = serializer;
        }


        @Override
        public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            return serializer.deserialize(jsonParser.readValueAs(serializer.outputClass()));
        }
    }

}

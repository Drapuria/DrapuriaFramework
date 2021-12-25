package net.drapuria.framework.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.AllArgsConstructor;
import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.jackson.JacksonConfigure;

@AllArgsConstructor
public class SerializerJacksonConfigure implements JacksonConfigure {

    private final ObjectSerializer<?, ?> serializer;

    @Override
    public void configure(ObjectMapper objectMapper) {
        final SimpleModule module = new SimpleModule();
        module.addSerializer(new SerializerFactory.JacksonSerailizer(serializer));
        module.addDeserializer(serializer.inputClass(), new SerializerFactory.JacksonDeserailizer(serializer));

        objectMapper.registerModule(module);
    }
}

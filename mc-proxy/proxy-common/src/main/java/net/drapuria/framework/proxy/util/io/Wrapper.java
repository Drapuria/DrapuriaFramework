package net.drapuria.framework.proxy.util.io;

import java.io.Serializable;
import java.util.Map;


import com.google.common.collect.ImmutableMap;
import net.drapuria.framework.proxy.util.configuration.serialization.ConfigurationSerializable;
import net.drapuria.framework.proxy.util.configuration.serialization.ConfigurationSerialization;

class Wrapper<T extends Map<String, ?> & Serializable> implements Serializable {
    private static final long serialVersionUID = -986209235411767547L;

    final T map;

    static Wrapper<ImmutableMap<String, ?>> newWrapper(ConfigurationSerializable obj) {
        return new Wrapper<>(ImmutableMap.<String, Object>builder().put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(obj.getClass())).putAll(obj.serialize()).build());
    }

    private Wrapper(T map) {
        this.map = map;
    }
}

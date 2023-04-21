package net.drapuria.framework.command.context;

import net.drapuria.framework.command.FrameworkCommand;
import net.drapuria.framework.command.meta.CommandMeta;

import java.util.HashMap;
import java.util.Map;

public class CommandContext<S, M extends CommandMeta>  {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<>();


    private final S source;
    private final String input;
    private final FrameworkCommand<M> command;


    public CommandContext(S source, String input, FrameworkCommand<M> command) {
        this.source = source;
        this.input = input;
        this.command = command;
    }

    static {
        PRIMITIVE_TO_WRAPPER.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(Byte.TYPE, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(Short.TYPE, Short.class);
        PRIMITIVE_TO_WRAPPER.put(Character.TYPE, Character.class);
        PRIMITIVE_TO_WRAPPER.put(Integer.TYPE, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(Long.TYPE, Long.class);
        PRIMITIVE_TO_WRAPPER.put(Float.TYPE, Float.class);
        PRIMITIVE_TO_WRAPPER.put(Double.TYPE, Double.class);
    }
}

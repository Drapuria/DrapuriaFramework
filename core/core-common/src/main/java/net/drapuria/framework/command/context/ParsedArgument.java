package net.drapuria.framework.command.context;

import lombok.Getter;

@Getter
public class ParsedArgument<T> {

    private final int position;
    private final T result;
    private final boolean wildcard;

    public ParsedArgument(int position, T result, boolean wildcard) {
        this.position = position;
        this.result = result;
        this.wildcard = wildcard;
    }
}

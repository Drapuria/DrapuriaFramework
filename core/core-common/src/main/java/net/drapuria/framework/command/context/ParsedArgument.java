package net.drapuria.framework.command.context;

import lombok.Getter;

@Getter
public class ParsedArgument<T> {

    private final int position;
    private final T result;

    public ParsedArgument(int position, T result) {
        this.position = position;
        this.result = result;
    }
}

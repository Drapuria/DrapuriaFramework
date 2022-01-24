/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.command.parser;

/**
 * @param <T> Type to parse
 * @param <S> Sender (e.G. Player)
 */
public interface CommandTypeParameterParser<T, S> {

    /**
     * @param sender The Command sender
     * @param source The {@link String source} we want to parse
     * @return Parsed Value
     */
    T parse(S sender, String source);

    Class<T> getType();

}

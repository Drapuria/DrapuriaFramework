/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.oauth2.exception;

public class InvalidStateException extends Exception {
    public InvalidStateException(String format) {
        super(format);
    }
}

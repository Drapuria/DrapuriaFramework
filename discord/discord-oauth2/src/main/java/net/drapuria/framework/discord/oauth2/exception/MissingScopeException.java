/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.oauth2.exception;

import net.drapuria.framework.discord.oauth2.Scope;

public class MissingScopeException extends Exception {

    public MissingScopeException(String string, Scope guilds) {
        super(string + ": " + guilds.getText());
    }
}

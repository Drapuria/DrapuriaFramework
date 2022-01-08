package net.drapuria.framework.discord.oauth2.session;

import net.drapuria.framework.discord.oauth2.Scope;

import java.time.OffsetDateTime;

public interface Session {


    String getAccessToken();

    String getRefreshToken();

    Scope[] getScopes();

    String getTokenType();

    OffsetDateTime getExpiration();

}

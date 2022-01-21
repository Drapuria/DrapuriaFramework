/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.oauth2.session;

import net.drapuria.framework.discord.oauth2.Scope;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class SessionData {

    private final String identifier, accessToken, refreshToken, tokenType;
    private final OffsetDateTime expiration;
    private final Scope[] scopes;

    public SessionData(String identifier, String accessToken, String refreshToken, String tokenType, OffsetDateTime expiration, Scope[] scopes)
    {
        this.identifier = identifier;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiration = expiration;
        this.scopes = scopes;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public String getTokenType()
    {
        return tokenType;
    }

    public OffsetDateTime getExpiration()
    {
        return expiration;
    }

    public Scope[] getScopes()
    {
        return scopes;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof SessionData))
            return false;

        SessionData data = ((SessionData) obj);

        return getIdentifier().equals(data.getIdentifier()) && getTokenType().equals(data.getTokenType());
    }

    @Override
    public String toString()
    {
        return String.format("SessionData(identifier: %s, access-token: %s, refresh-token: %s, type: %s, expires: %s)",
                getIdentifier(), getAccessToken(), getRefreshToken(), getTokenType(),
                getExpiration().format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

}

/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.oauth2.session;

import net.drapuria.framework.discord.oauth2.Scope;

import java.time.OffsetDateTime;
import java.util.HashMap;

public class DefaultSessionController implements SessionController<DefaultSessionController.DefaultSession> {
    private final HashMap<String, DefaultSession> sessions = new HashMap<>();

    @Override
    public DefaultSession getSession(String identifier) {
        return sessions.get(identifier);
    }

    @Override
    public DefaultSession createSession(SessionData data) {
        DefaultSession created = new DefaultSession(data);
        sessions.put(data.getIdentifier(), created);
        return created;
    }

    public class DefaultSession implements Session {
        private final String accessToken, refreshToken, tokenType;
        private final OffsetDateTime expiration;
        private final Scope[] scopes;

        private DefaultSession(String accessToken, String refreshToken, String tokenType, OffsetDateTime expiration, Scope[] scopes) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.tokenType = tokenType;
            this.expiration = expiration;
            this.scopes = scopes;
        }

        private DefaultSession(SessionData data) {
            this(data.getAccessToken(), data.getRefreshToken(), data.getTokenType(), data.getExpiration(), data.getScopes());
        }

        @Override
        public String getAccessToken() {
            return accessToken;
        }

        @Override
        public String getRefreshToken() {
            return refreshToken;
        }

        @Override
        public Scope[] getScopes() {
            return scopes;
        }

        @Override
        public String getTokenType() {
            return tokenType;
        }

        @Override
        public OffsetDateTime getExpiration() {
            return expiration;
        }
    }
}

package net.drapuria.framework.discord.oauth2;

import net.drapuria.framework.discord.oauth2.entity.OAuth2Connection;
import net.drapuria.framework.discord.oauth2.entity.OAuth2Guild;
import net.drapuria.framework.discord.oauth2.entity.OAuth2User;
import net.drapuria.framework.discord.oauth2.entity.impl.OAuth2ClientImpl;
import net.drapuria.framework.discord.oauth2.exception.InvalidStateException;
import net.drapuria.framework.discord.oauth2.exception.MissingScopeException;
import net.drapuria.framework.discord.oauth2.request.OAuth2Action;
import net.drapuria.framework.discord.oauth2.session.Session;
import net.drapuria.framework.discord.oauth2.session.SessionController;
import net.drapuria.framework.discord.oauth2.state.StateController;
import okhttp3.OkHttpClient;

import java.util.List;

public interface OAuth2Client {

    int DISCORD_REST_VERSION = 8;

    /**
     * @param redirectUri Uri we redirect to
     * @param scopes used scopes
     * @return generated auth url
     */
    String generateAuthorizationUrl(String redirectUri, Scope... scopes);

    /**
     * @param code
     * @param state
     * @param identifier
     * @param scopes
     * @return
     * @throws InvalidStateException
     */
    OAuth2Action<Session> startSession(String code, String state, String identifier, Scope... scopes) throws InvalidStateException;

    /**
     * @param session
     * @return
     */
    OAuth2Action<OAuth2User> getUser(Session session);

    OAuth2Action<List<OAuth2Guild>> getGuilds(Session session) throws MissingScopeException;

    OAuth2Action<List<OAuth2Connection>> getConnections(Session session) throws MissingScopeException;

    long getId();

    String getSecret();

    StateController getStateController();

    SessionController getSessionController();


    class Builder
    {
        private long clientId = -1;
        private String clientSecret;
        private SessionController sessionController;
        private StateController stateController;
        private OkHttpClient client;


        public OAuth2Client build()
        {
            return new OAuth2ClientImpl(clientId, clientSecret, sessionController, stateController, client);
        }


        public Builder setClientId(long clientId)
        {
            this.clientId = clientId;
            return this;
        }

        public Builder setClientSecret(String clientSecret)
        {
            this.clientSecret = clientSecret;
            return this;
        }


        public Builder setSessionController(SessionController sessionController)
        {
            this.sessionController = sessionController;
            return this;
        }


        public Builder setStateController(StateController stateController)
        {
            this.stateController = stateController;
            return this;
        }


        public Builder setOkHttpClient(OkHttpClient client)
        {
            this.client = client;
            return this;
        }
    }


}

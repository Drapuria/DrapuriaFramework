package net.drapuria.framework.discord.oauth2.session;

public interface SessionController<S extends Session> {

    S getSession(String identifier);

    S createSession(SessionData data);

}

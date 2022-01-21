/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.oauth2.entity.impl;

import lombok.Getter;
import net.drapuria.framework.discord.oauth2.OAuth2Client;
import net.drapuria.framework.discord.oauth2.Scope;
import net.drapuria.framework.discord.oauth2.entity.OAuth2Connection;
import net.drapuria.framework.discord.oauth2.entity.OAuth2Guild;
import net.drapuria.framework.discord.oauth2.entity.OAuth2User;
import net.drapuria.framework.discord.oauth2.exception.InvalidStateException;
import net.drapuria.framework.discord.oauth2.exception.MissingScopeException;
import net.drapuria.framework.discord.oauth2.request.OAuth2Action;
import net.drapuria.framework.discord.oauth2.request.OAuth2Requester;
import net.drapuria.framework.discord.oauth2.request.OAuth2URL;
import net.drapuria.framework.discord.oauth2.request.RequestMethod;
import net.drapuria.framework.discord.oauth2.session.Session;
import net.drapuria.framework.discord.oauth2.session.SessionController;
import net.drapuria.framework.discord.oauth2.session.SessionData;
import net.drapuria.framework.discord.oauth2.state.StateController;
import net.drapuria.framework.discord.oauth2.util.EncodingUtil;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

@Getter
public class OAuth2ClientImpl implements OAuth2Client {

    private final long clientId;
    private final String clientSecret;
    private final SessionController sessionController;
    private final StateController stateController;
    private final OkHttpClient httpClient;
    private final OAuth2Requester requester;

    public OAuth2ClientImpl(long clientId, String clientSecret, SessionController sessionController, StateController stateController, OkHttpClient httpClient) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.sessionController = sessionController;
        this.stateController = stateController;
        this.httpClient = httpClient;
        this.requester = new OAuth2Requester(this.httpClient);
    }

    @Override
    public String[] generateAuthorizationUrl(String redirectUri, Scope... scopes) {
        String state = stateController.generateNewState(redirectUri);
        return new String[]{OAuth2URL.AUTHORIZE.compile(clientId, EncodingUtil.encodeUTF8(redirectUri),
                Scope.join(scopes), state), state};
    }

    @Override
    public OAuth2Action<Session> startSession(String code, String state, String identifier, Scope... scopes) throws InvalidStateException {


        final String redirectUri = stateController.consumeState(state);
        if (redirectUri == null)
            throw new InvalidStateException(String.format("No state '%s' exists!", state));

        OAuth2URL oAuth2URL = OAuth2URL.TOKEN;

        return new OAuth2Action<Session>(this, RequestMethod.POST, oAuth2URL.getRouteWithBaseUrl()) {
            @Override
            protected Headers getHeaders() {
                return Headers.of("Content-Type", "x-www-form-urlencoded");
            }

            @Override
            protected RequestBody getBody() {
                return RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        oAuth2URL.compileQueryParams(clientId, EncodingUtil.encodeUTF8(redirectUri), code, clientSecret,
                                Scope.join(true, scopes)));
            }

            @Override
            protected Session handle(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw failure(response);

                JSONObject body = new JSONObject(new JSONTokener(OAuth2ClientImpl.getBody(response)));

                String[] scopeStrings = body.getString("scope").split(" ");
                Scope[] scopes = Arrays.stream(scopeStrings).map(Scope::from).toArray(Scope[]::new);

                return sessionController.createSession(new SessionData(identifier,
                        body.getString("access_token"), body.getString("refresh_token"),
                        body.getString("token_type"), OffsetDateTime.now().plusSeconds(body.getInt("expires_in")), scopes));
            }
        };
    }

    @Override
    public OAuth2Action<OAuth2User> getUser(Session session) {
        return new OAuth2Action<OAuth2User>(this, RequestMethod.GET, OAuth2URL.CURRENT_USER.compile()) {
            @Override
            protected Headers getHeaders() {
                return Headers.of("Authorization", generateAuthorizationHeader(session));
            }

            @Override
            protected OAuth2User handle(Response response) throws Exception {
                if (!response.isSuccessful())
                    throw failure(response);
                JSONObject body = new JSONObject(new JSONTokener(OAuth2ClientImpl.getBody(response)));
                return new OAuth2UserImpl(OAuth2ClientImpl.this, session, body.getLong("id"),
                        body.getString("username"), body.getString("discriminator"),
                        body.optString("avatar", null), body.optString("email", null),
                        body.optBoolean("verified", false), body.getBoolean("mfa_enabled"),
                        body.optString("banner", null), body.optString("locale"),
                        body.optInt("premium_type"));
            }
        };
    }

    @Override
    public OAuth2Action<List<OAuth2Guild>> getGuilds(Session session) throws MissingScopeException {
        if (!Scope.contains(session.getScopes(), Scope.GUILDS))
            throw new MissingScopeException("Missing scope for session", Scope.GUILDS);
        return new OAuth2Action<List<OAuth2Guild>>(this, RequestMethod.GET, OAuth2URL.CURRENT_USER_GUILDS.compile()) {
            @Override
            protected Headers getHeaders() {
                return Headers.of("Authorization", generateAuthorizationHeader(session));
            }

            @Override
            protected List<OAuth2Guild> handle(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw failure(response);

                JSONArray body = new JSONArray(new JSONTokener(OAuth2ClientImpl.getBody(response)));
                List<OAuth2Guild> list = new LinkedList<>();
                JSONObject object;
                for (int i = 0; i < body.length(); i++) {
                    object = body.getJSONObject(i);
                    list.add(new OAuth2GuildImpl(OAuth2ClientImpl.this,
                            object.getLong("id"),
                            object.getString("name"),
                            object.optString("icon", null),
                            object.getBoolean("owner"),
                            object.getLong("permissions")));
                }
                return list;
            }
        };
    }

    @Override
    public OAuth2Action<List<OAuth2Connection>> getConnections(Session session) throws MissingScopeException {
        if (!Scope.contains(session.getScopes(), Scope.CONNECTIONS))
            throw new MissingScopeException("Missing scope for session", Scope.CONNECTIONS);
        return new OAuth2Action<List<OAuth2Connection>>(this, RequestMethod.GET, OAuth2URL.CURRENT_USER_CONNECTIONS.compile()) {
            @Override
            protected Headers getHeaders() {
                return Headers.of("Authorization", generateAuthorizationHeader(session));
            }

            @Override
            protected List<OAuth2Connection> handle(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw failure(response);
                JSONArray body = new JSONArray(new JSONTokener(OAuth2ClientImpl.getBody(response)));
                List<OAuth2Connection> list = new LinkedList<>();
                JSONObject object;
                for (int i = 0; i < body.length(); i++) {
                    object = body.getJSONObject(i);
                    list.add(new OAuth2ConnectionImpl(OAuth2ClientImpl.this,
                            object.getString("id"),
                            object.getString("name"),
                            object.getString("type"),
                            object.getBoolean("verified"),
                            object.getBoolean("friend_sync"),
                            object.getInt("visibility")));
                }
                return list;
            }
        };
    }

    @Override
    public OAuth2Action<Boolean> joinGuild(String botToken, Session session, long guildId, OAuth2User user, String nick, JSONArray roles) {
        return new OAuth2Action<Boolean>(this, RequestMethod.PUT, String.format(OAuth2URL.GUILD_JOIN.compile(guildId, user.getIdLong()), guildId, user.getIdLong())) {
            @Override
            protected Headers getHeaders() {
                return Headers.of("Authorization", "Bot " + botToken, "Content-Type", "application/json", "access_token", generateAuthorizationHeader(session));
            }

            @Override
            protected RequestBody getBody() {
                JSONObject object = new JSONObject();
                object.put("access_token", session.getAccessToken());
                if (nick != null)
                    object.put("nick", nick);
                if (roles != null)
                    object.put("roles", roles);
                return RequestBody.create(MediaType.parse("application/json"), object.toString());
            }

            @Override
            protected Boolean handle(Response response) throws IOException, Exception {
                if (!response.isSuccessful())
                    throw failure(response);
                JSONObject body = new JSONObject(new JSONTokener(OAuth2ClientImpl.getBody(response)));
                System.out.println(body);
                return true;
            }
        };
    }

    public void shutdown() {
        httpClient.dispatcher().executorService().shutdown();
    }

    @Override
    public long getId() {
        return clientId;
    }

    @Override
    public String getSecret() {
        return this.clientSecret;
    }

    protected static IOException failure(Response response) throws IOException {
        final InputStream stream = getBody(response);
        final String responseBody = new String(readFully(stream));
        return new IOException("Request returned failure " + response.code() + ": " + responseBody);
    }

    public static InputStream getBody(Response response) throws IOException {
        String encoding = response.header("content-encoding", "");
        InputStream data = new BufferedInputStream(response.body().byteStream());
        data.mark(256);

        try {
            if (encoding.equalsIgnoreCase("gzip")) {
                return new GZIPInputStream(data);
            } else {
                return encoding.equalsIgnoreCase("deflate") ? new InflaterInputStream(data, new Inflater(true)) : data;
            }
        } catch (EOFException | ZipException var4) {
            data.reset();
            System.out.println("Failed to read gzip content for response. Headers: {}\nContent: '{}'" + Arrays.toString(new Object[]{response.headers(), getLazyString(() -> new String(readFully(data))), var4}));
            return null;
        }
    }

    public static byte[] readFully(InputStream stream) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] var4;
        try {

            while (true) {
                int readAmount;
                if ((readAmount = stream.read(buffer)) == -1) {
                    var4 = bos.toByteArray();
                    break;
                }

                bos.write(buffer, 0, readAmount);
            }
        } catch (Throwable var6) {
            try {
                bos.close();
            } catch (Throwable var5) {
                var6.addSuppressed(var5);
            }

            throw var6;
        }

        bos.close();
        return var4;
    }

    private String generateAuthorizationHeader(Session session) {
        return String.format("%s %s", session.getTokenType(), session.getAccessToken());
    }

    public static Object getLazyString(final LazyEvaluation lazyLambda) {
        return new Object() {
            public String toString() {
                try {
                    return lazyLambda.getString();
                } catch (Exception var3) {
                    StringWriter sw = new StringWriter();
                    var3.printStackTrace(new PrintWriter(sw));
                    return "Error while evaluating lazy String... " + sw.toString();
                }
            }
        };
    }

    @FunctionalInterface
    public interface LazyEvaluation {
        String getString() throws Exception;
    }

}

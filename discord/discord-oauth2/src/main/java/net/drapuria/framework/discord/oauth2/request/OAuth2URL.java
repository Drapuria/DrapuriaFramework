/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.oauth2.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.drapuria.framework.discord.oauth2.OAuth2Client;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
@Getter
public enum OAuth2URL {

    AUTHORIZE("/oauth2/authorize",
            "client_id=%d",
            "redirect_uri=%s",
            "response_type=code",
            "scope=%s",
            "state=%s"),
    TOKEN("/oauth2/token",
            "client_id=%d",
            "redirect_uri=%s",
            "grant_type=authorization_code",
            "code=%s",
            "client_secret=%s",
            "scope=%s"),
    CURRENT_USER("/users/@me"),
    CURRENT_USER_CONNECTIONS("/users/@me/connections"),
    CURRENT_USER_GUILDS("/users/@me/guilds"),
    GUILD_JOIN("/guilds/%s/members/%s");

    public static final String BASE_URL = String.format("https://discord.com/api/v%d", OAuth2Client.DISCORD_REST_VERSION);


    private final String route;
    private final String formattableRoute;
    private final boolean hasQueryParams;
    private final String queryParams;

    OAuth2URL(String route, String... queryParams) {
        this.route = route;
        this.hasQueryParams = queryParams.length > 0;

        if (hasQueryParams) {
            String b = IntStream.range(0, queryParams.length)
                    .mapToObj(i -> (i == 0 ? '?' : '&') + queryParams[i]).collect(Collectors.joining());
            this.formattableRoute = route + b;
            this.queryParams = b;
        } else {
            this.formattableRoute = route;
            this.queryParams = "";
        }
    }

    public String getRouteWithBaseUrl() {
        return BASE_URL + route;
    }


    public String compileQueryParams(Object... values) {
        return String.format(queryParams, values).replaceFirst("\\?", "");
    }

    public String compile(Object... values) {
        return BASE_URL + (this.hasQueryParams ? String.format(formattableRoute, values) : formattableRoute);
    }

}

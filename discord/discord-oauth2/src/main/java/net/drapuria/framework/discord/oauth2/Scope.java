package net.drapuria.framework.discord.oauth2;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Scope {

    BOT("bot"),

    CONNECTIONS("connections"),

    IDENTIFY("identify"),

    EMAIL("email"),

    GUILDS("guilds"),

    GUILDS_JOIN("guilds.join"),

    GDM_JOIN("gdm.join"),

    MESSAGES_READ("messages.read"),

    RPC("rpc"),

    RPC_API("rpc.api"),

    RPC_NOTIFICATIONS_READ("rpc.notifications.read"),

    WEBHOOK_INCOMING("webhook.incoming"),

    UNKNOWN("");

    private final String text;

    public static boolean contains(Scope[] scopes, Scope scope) {

        if (scopes == null || scopes.length == 0 || scope == null || scope == UNKNOWN)
            return false;
        return Arrays.stream(scopes).anyMatch(s -> s == scope);
    }

    public static String join(Scope... scopes) {
        return join(false, scopes);
    }

    public static String join(boolean bySpace, Scope... scopes) {
        if (scopes.length == 0)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < scopes.length; i++) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(bySpace ? " " : "%20");
            stringBuilder.append(scopes[i].getText());
        }
        return stringBuilder.toString();
    }

    public static Scope from(String scope) {
        return Arrays.stream(values()).filter(s -> s.getText().equalsIgnoreCase(scope)).findFirst().orElse(UNKNOWN);
    }

}

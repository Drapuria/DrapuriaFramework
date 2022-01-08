package net.drapuria.framework.discord.oauth2.entity;

import net.drapuria.framework.discord.oauth2.OAuth2Client;
import net.drapuria.framework.discord.oauth2.exception.MissingScopeException;
import net.drapuria.framework.discord.oauth2.session.Session;

public interface OAuth2User {

    OAuth2Client getClient();

    Session getSession();

    String getId();

    long getIdLong();

    String getName();

    String getEmail() throws MissingScopeException;

    boolean isVerified();

    boolean hasMfaEnabled();

    String getDiscriminator();

    String getAvatarId();

    String getAvatarUrl();


    String getDefaultAvatarId();

    String getDefaultAvatarUrl();

    String getEffectiveAvatarUrl();

    default boolean isBot() {
        return false;
    }

    String getAsMention();


}

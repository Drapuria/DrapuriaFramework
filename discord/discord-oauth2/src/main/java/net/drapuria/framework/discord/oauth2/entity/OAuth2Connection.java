package net.drapuria.framework.discord.oauth2.entity;

import net.drapuria.framework.discord.oauth2.OAuth2Client;

public interface OAuth2Connection {

    String getId();

    String getName();

    String getType();

    boolean isVerified();

    boolean isFriendSync();

    Integer getVisibility();

    OAuth2Client getClient();


}

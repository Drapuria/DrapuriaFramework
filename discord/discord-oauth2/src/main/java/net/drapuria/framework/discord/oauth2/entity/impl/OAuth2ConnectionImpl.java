/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.oauth2.entity.impl;

import net.drapuria.framework.discord.oauth2.OAuth2Client;
import net.drapuria.framework.discord.oauth2.entity.OAuth2Connection;

public class OAuth2ConnectionImpl implements OAuth2Connection {

    private final OAuth2Client client;
    private final String id, name, type;
    private final boolean verified, friendSync;
    private final Integer visibility;

    public OAuth2ConnectionImpl(OAuth2Client client, String id, String name, String type, boolean verified, boolean friendSync, Integer visibility) {
        this.client = client;
        this.id = id;
        this.name = name;
        this.type = type;
        this.verified = verified;
        this.friendSync = friendSync;
        this.visibility = visibility;
    }


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public boolean isVerified() {
        return this.verified;
    }

    @Override
    public boolean isFriendSync() {
        return this.friendSync;
    }

    @Override
    public Integer getVisibility() {
        return this.visibility;
    }

    @Override
    public OAuth2Client getClient() {
        return this.client;
    }

    @Override
    public String toString() {
        return "OAuth2ConnectionImpl{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", verified=" + verified +
                ", friendSync=" + friendSync +
                ", visibility=" + visibility +
                '}';
    }
}

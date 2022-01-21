/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.oauth2.entity;

import net.drapuria.framework.discord.oauth2.OAuth2Client;
import net.drapuria.framework.discord.oauth2.permission.Permission;

import java.util.EnumSet;

public interface OAuth2Guild {

    OAuth2Client getClient();

    long getIdLong();

    String getId();

    String getName();

    String getIconId();

    String getIconUrl();

    long getPermissionsRaw();

    EnumSet<Permission> getPermissions();

    boolean isOwner();

    boolean hasPermission(Permission... permissions);


}

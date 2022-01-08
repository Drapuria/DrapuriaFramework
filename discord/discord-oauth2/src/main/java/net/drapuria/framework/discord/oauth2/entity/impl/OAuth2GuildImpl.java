package net.drapuria.framework.discord.oauth2.entity.impl;

import net.drapuria.framework.discord.oauth2.OAuth2Client;
import net.drapuria.framework.discord.oauth2.entity.OAuth2Guild;
import net.drapuria.framework.discord.oauth2.permission.Permission;

import java.util.EnumSet;

public class OAuth2GuildImpl implements OAuth2Guild {

    private final OAuth2Client client;
    private final long id;
    private final String name, icon;
    private final boolean owner;
    private final int permissions;

    public OAuth2GuildImpl(OAuth2Client client, long id, String name, String icon, boolean owner, int permissions) {
        this.client = client;
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.owner = owner;
        this.permissions = permissions;
    }

    @Override
    public OAuth2Client getClient() {
        return this.client;
    }

    @Override
    public long getIdLong() {
        return this.id;
    }

    @Override
    public String getId() {
        return Long.toUnsignedString(this.id);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getIconId() {
        return this.icon;
    }

    @Override
    public String getIconUrl() {
        return this.icon == null ? null : "https://cdn.discordapp.com/icons/" + this.id + "/" + this.icon + ".png";
    }

    @Override
    public int getPermissionsRaw() {
        return this.permissions;
    }

    @Override
    public EnumSet<Permission> getPermissions() {
        return Permission.getPermissions(this.permissions);
    }

    @Override
    public boolean isOwner() {
        return this.owner;
    }

    @Override
    public boolean hasPermission(Permission... perms) {
        if (isOwner())
            return true;

        long adminPermRaw = Permission.ADMINISTRATOR.getRawValue();
        if ((permissions & adminPermRaw) == adminPermRaw)
            return true;

        long checkPermsRaw = Permission.getRaw(perms);
        return (permissions & checkPermsRaw) == checkPermsRaw;
    }
}

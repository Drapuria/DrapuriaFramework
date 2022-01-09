package net.drapuria.framework.discord.oauth2.entity.impl;

import net.drapuria.framework.discord.oauth2.OAuth2Client;
import net.drapuria.framework.discord.oauth2.Scope;
import net.drapuria.framework.discord.oauth2.entity.OAuth2User;
import net.drapuria.framework.discord.oauth2.exception.MissingScopeException;
import net.drapuria.framework.discord.oauth2.session.Session;

import java.util.Locale;
import java.util.Objects;

public class OAuth2UserImpl implements OAuth2User {

    private static final String[] DEFAULT_AVATARS = new String[] {
            "6debd47ed13483642cf09e832ed0bc1b",
            "322c936a8c8be1b803cd94861bdfa868",
            "dd4dbc0016779df1378e7812eabaa04d",
            "0e291f67c9274a1abdddeb3fd919cbaa",
            "1cbd08c76f8af6dddce02c5138971129"
    };

    private final OAuth2Client client;
    private final Session session;
    private final long id;
    private final String name, discriminator, avatar, email, locale, banner;
    private final boolean verified, mfaEnabled;
    private final int premiumType;


    public OAuth2UserImpl(OAuth2Client client, Session session, long id, String name, String discriminator,
                          String avatar, String email, boolean verified, boolean mfaEnabled, String banner,
                          String locale, int premiumType) {
        this.client = client;
        this.session = session;
        this.id = id;
        this.name = name;
        this.discriminator = discriminator;
        this.avatar = avatar;
        this.email = email;
        this.verified = verified;
        this.mfaEnabled = mfaEnabled;
        this.banner = banner;
        this.locale = locale;
        this.premiumType = premiumType;
    }

    @Override
    public OAuth2Client getClient() {
        return this.client;
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public String getId() {
        return Long.toUnsignedString(this.id);
    }

    @Override
    public long getIdLong() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEmail() throws MissingScopeException {
        if (!Scope.contains(getSession().getScopes(), Scope.EMAIL))
            throw new MissingScopeException("get email for user", Scope.EMAIL);
        return this.email;
    }

    @Override
    public boolean isVerified() {
        return this.verified;
    }

    @Override
    public boolean hasMfaEnabled() {
        return this.mfaEnabled;
    }

    @Override
    public String getDiscriminator() {
        return this.discriminator;
    }

    @Override
    public String getAvatarId() {
        return this.avatar;
    }

    @Override
    public String getAvatarUrl() {
        return getAvatarId() == null ? null : "https://cdn.discordapp.com/avatars/" + getId() + "/" + getAvatarId()
                + (getAvatarId().startsWith("a_") ? ".gif" : ".png");
    }

    @Override
    public String getDefaultAvatarId() {
        return DEFAULT_AVATARS[Integer.parseInt(getDiscriminator()) % DEFAULT_AVATARS.length];
    }

    @Override
    public String getDefaultAvatarUrl() {
        return "https://discord.com/assets/" + getDefaultAvatarId() + ".png";
    }

    @Override
    public String getBannerId() {
        return this.banner;
    }

    @Override
    public String getBannerUrl() {
        return getBannerId() == null ? null : "https://cdn.discordapp.com/banners/" + getId() + "/" + getBannerId()
                + (getBannerId().startsWith("a_") ? ".gif" : ".png");
    }

    @Override
    public String getEffectiveAvatarUrl() {
        return getAvatarUrl() == null ? getDefaultAvatarUrl() : getAvatarUrl();
    }

    @Override
    public String getAsMention() {
        return "<@" + id + '>';
    }

    @Override
    public Integer getPremiumType() {
        return this.premiumType;
    }

    @Override
    public Locale getLocale() {
        return Locale.forLanguageTag(locale);
    }

    @Override
    public String getLocaleString() {
        return this.locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuth2UserImpl that = (OAuth2UserImpl) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OAuth2UserImpl{" +
                "client=" + client +
                ", session=" + session +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", discriminator='" + discriminator + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", verified=" + verified +
                ", mfaEnabled=" + mfaEnabled +
                '}';
    }
}

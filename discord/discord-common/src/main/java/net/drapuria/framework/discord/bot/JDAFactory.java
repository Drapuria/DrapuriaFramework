/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.discord.bot;

import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.discord.configuration.AbstractDiscordBotConfiguration;
import net.drapuria.framework.discord.message.CachedMessage;
import net.drapuria.framework.discord.message.impl.CachedJDAMessage;
import net.drapuria.framework.libraries.Library;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;

public class JDAFactory extends DiscordBotFactory<JDA> {

    private final AbstractDiscordBotConfiguration<?> configuration;

    private JDA jda;
    private MemberCachePolicy memberCachePolicy = null;
    private ChunkingFilter chunkingFilter = null;
    private Activity activity = null;
    private OnlineStatus onlineStatus = null;
    private final Set<GatewayIntent> intents = new HashSet<>();
    private final Set<CacheFlag> cacheFlags = new HashSet<>();
    private final Set<CacheFlag> disableCacheFlags = new HashSet<>();
    public JDAFactory(AbstractDiscordBotConfiguration<?> configuration) {
        this.configuration = configuration;
    }


    @SneakyThrows
    @Override
    public JDA create() {
        assert jda == null;
        if (activity == null)
            activity = Activity.playing("Bot build by Drapuria.net");
        if (onlineStatus == null)
            onlineStatus = OnlineStatus.ONLINE;
        if (memberCachePolicy == null)
            memberCachePolicy = MemberCachePolicy.DEFAULT;
        if (chunkingFilter == null)
            chunkingFilter = ChunkingFilter.NONE;
        this.jda = JDABuilder.createDefault(configuration.token(), intents)
                .setMemberCachePolicy(memberCachePolicy)
                .enableCache(cacheFlags)
                .disableCache(cacheFlags)
                .setChunkingFilter(chunkingFilter)
                .setActivity(activity)
                .setStatus(onlineStatus)
                .setAutoReconnect(true)
                .build();
        jda.awaitReady();
        CachedJDAMessage.factory = this;
        return this.jda;
    }

    public JDAFactory addIntent(GatewayIntent intent) {
        this.intents.add(intent);
        return this;
    }

    public JDAFactory removeIntent(final GatewayIntent intent) {
        this.intents.remove(intent);
        return this;
    }

    public JDAFactory addIntents(Collection<GatewayIntent> intents) {
        this.intents.addAll(intents);
        return this;
    }

    public JDAFactory clearIntents(final GatewayIntent intent) {
        this.intents.clear();
        return this;
    }

    public JDAFactory setChunkingFilter(ChunkingFilter chunkingFilter) {
        this.chunkingFilter = chunkingFilter;
        return this;
    }

    public JDAFactory addCacheFlag(final CacheFlag cacheFlag) {
        this.cacheFlags.add(cacheFlag);
        return this;
    }

    public JDAFactory disableCacheFlag(final CacheFlag cacheFlag) {
        this.disableCacheFlags.remove(cacheFlag);
        return this;
    }

    public JDAFactory setMemberCachePolicy(MemberCachePolicy cachePolicy) {
        this.memberCachePolicy = cachePolicy;
        return this;
    }

    public JDAFactory setOnlineStatus(OnlineStatus onlineStatus) {
        this.onlineStatus = onlineStatus;
        return this;
    }

    public JDAFactory setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    @Override
    public void shutdown() {
        if (this.jda != null) {
            this.jda.shutdownNow();
        }
    }

    @Override
    public JDA get() {
        return jda;
    }

    @Override
    public JDAFactory setupLibraries() {
        try {
            Class.forName("net.dv8tion.jda.api.JDA");
        } catch (Exception ignored) {
            Library jdaLibrary = new Library("net.dv8tion", "JDA", "5.0.0-alpha.21", null);
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, jdaLibrary);
        }

        try {
            Class.forName("okio.Buffer");
        } catch (Exception ignored) {
            Library okioLib = new Library("com.squareup.okio", "okio", "2.8.0", null);
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true,  okioLib);
        }
        try {
            Class.forName("okhttp3.Request");
        } catch (Exception ignored) {
            Library httpLib = new Library("com.squareup.okhttp3", "okhttp", "4.9.3", null);
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, httpLib);
        }
        try {
            Class.forName("org.apache.commons.collections4.collection.CompositeCollection");
        } catch (Exception ignored) {
            Library apacheLib = new Library("org.apache.commons", "commons-collections4", "4.4", null);
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, apacheLib);
        }
        try {
            Class.forName("com.neovisionaries.ws.client.WebSocket");
        } catch (Exception ignored) {
            Library library = new Library("com.neovisionaries", "nv-websocket-client", "2.14", null);
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, library);
        }
        try {
            Class.forName("gnu.trove.map.TByteLongMap");
        } catch (Exception ignored) {
            /*
            <dependency>
    <groupId>gnu.trove</groupId>
    <artifactId>trove</artifactId>
    <version>3.0.3</version>
</dependency>

             */
            Library library = new Library("gnu.trove", "trove", "3.0.3", "3.0.3", null,
                    "http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-releases/");
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, library);
        }
        return this;
    }
}

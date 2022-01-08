package net.drapuria.framework.discord.bot;

import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.discord.configuration.AbstractDiscordBotConfiguration;
import net.drapuria.framework.discord.message.impl.CachedJDAMessage;
import net.drapuria.framework.libraries.Library;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class JDAFactory extends DiscordBotFactory<JDA> {

    private final AbstractDiscordBotConfiguration<?> configuration;

    private JDA jda;

    public JDAFactory(AbstractDiscordBotConfiguration<?> configuration) {
        this.configuration = configuration;
    }


    @SneakyThrows
    @Override
    public JDA create() {
        assert jda == null;
        this.jda = JDABuilder.createDefault(configuration.token())
                .build();
        jda.awaitReady();
        CachedJDAMessage.factory = this;
        return this.jda;
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
    public void setupLibraries() {
        try {
            Class.forName("net.dv8tion.jda.api.JDA");
        } catch (Exception ignored) {
            Library jdaLibrary = new Library("net.dv8tion", "JDA", "5.0.0-alpha.3", null);
            DrapuriaCommon.LIBRARY_HANDLER.downloadLibraries(true, jdaLibrary);
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
    }
}

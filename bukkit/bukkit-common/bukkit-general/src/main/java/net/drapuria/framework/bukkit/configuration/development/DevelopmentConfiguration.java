package net.drapuria.framework.bukkit.configuration.development;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.drapuria.framework.configuration.yaml.annotation.Comment;
import net.drapuria.framework.configuration.yaml.annotation.ConfigurationElement;

@ConfigurationElement
@NoArgsConstructor
@Data
public class DevelopmentConfiguration {

    private Restart restartIfUpdateFolderNotEmpty = new Restart();
    private Logging logging = new Logging();

    @Getter
    @Setter
    @ConfigurationElement
    public static class Restart {
        private boolean enabled;
        private long checkDelay = 100L;
    }

    @Getter
    @Setter
    @ConfigurationElement
    public static class Logging {
        private boolean enabled;
        @Comment(value = {"Valid Logging modes are", "CHAT_IF_PLAYER", "CHAT", "CONSOLE", "Be aware that 'CHAT__PLAYER' only logs to players who have enabled logging with /drapurialogging addme"})
        private LoggingMode mode = LoggingMode.CONSOLE;
    }
    public static enum LoggingMode {
        CHAT_PLAYER,
        CHAT,
        CONSOLE
    }
}

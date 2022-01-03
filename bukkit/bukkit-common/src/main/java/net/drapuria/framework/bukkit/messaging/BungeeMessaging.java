package net.drapuria.framework.bukkit.messaging;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.UtilityClass;
import net.drapuria.framework.bukkit.Drapuria;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

@UtilityClass
public class BungeeMessaging {

    public void init() {
        Drapuria.PLUGIN.getServer().getMessenger().registerOutgoingPluginChannel(Drapuria.PLUGIN, "BungeeCord");
    }

    public void kickPlayer(Player player, String reason) {
        kickPlayer(player, player.getName(), reason);
    }

    public void kickPlayer(Player player, String playerName, String reason) {
        Validate.notNull(player, playerName, reason, "Input values cannot be null!");
        ByteArrayDataOutput output = createNewByteStream();
        output.writeUTF("KickPlayer");
        output.writeUTF(playerName);
        output.writeUTF(reason);
        sendMessage(player, output);
    }

    public void requestOnlinePlayers(final Player player) {
        Validate.notNull(player, "Input values cannot be null!");
        ByteArrayDataOutput output = createNewByteStream();
        output.writeUTF("PlayerCount");
        output.writeUTF("ALL");
        sendMessage(player, output);
    }

    public void requestMaxProxyPlayers(final Player player) {
        Validate.notNull(player, "Input values cannot be null!");
        ByteArrayDataOutput output = createNewByteStream();
        output.writeUTF("MaxPlayers");
        output.writeUTF("ALL");
        output.writeUTF(player.getName());
        sendMessage(player, output);
    }

    public void sendToServer(Player player, String serverName) {
        Validate.notNull(player, serverName, "Input values cannot be null!");
        ByteArrayDataOutput output = createNewByteStream();
        try {
            output.writeUTF("Connect");
            output.writeUTF(serverName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMessage(player, output);
    }

    private void sendMessage(Player player, ByteArrayDataOutput output) {
        player.sendPluginMessage(Drapuria.PLUGIN, "BungeeCord", output.toByteArray());
    }

    @SuppressWarnings("UnstableApiUsage")
    private ByteArrayDataOutput createNewByteStream() {
        return ByteStreams.newDataOutput();
    }
}

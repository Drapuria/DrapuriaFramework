package net.drapuria.framework.bukkit.tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.drapuria.framework.bukkit.util.CC;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class SimpleHeaderFooter {

    private final Player player;
    private String header, footer;

    private boolean destroying;

    public SimpleHeaderFooter(final Player player) {
        this.header = "";
        this.footer = "";
        this.player = player;
    }

    public void update() {
        if (destroying)
            return;
        DrapuriaTabAdapter adapter = DrapuriaTabHandler.getInstance().getDefaultAdapter();
        String headerNow = CC.translate(adapter.getHeader(player));
        String footerNow = CC.translate(adapter.getFooter(player));
        if (!headerNow.equals(this.header) || !footerNow.equals(this.footer)) {
            updateHeaderAndFooter(headerNow, footerNow);
            this.header = headerNow;
            this.footer = footerNow;
        }
    }

    public void destroy() {
        destroying = true;
        PacketContainer headerAndFooter = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        headerAndFooter.getChatComponents().write(0, WrappedChatComponent.fromText(""));
        headerAndFooter.getChatComponents().write(1, WrappedChatComponent.fromText(""));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, headerAndFooter);
        } catch (InvocationTargetException ignored) {
        }
    }

    public void setDestroying(boolean destroying) {
        this.destroying = destroying;
    }

    private void updateHeaderAndFooter(String header, String footer) {
        PacketContainer headerAndFooter = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        headerAndFooter.getChatComponents().write(0, WrappedChatComponent.fromText(header));
        headerAndFooter.getChatComponents().write(1, WrappedChatComponent.fromText(footer));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, headerAndFooter);
        } catch (InvocationTargetException ignored) {
        }
    }

}

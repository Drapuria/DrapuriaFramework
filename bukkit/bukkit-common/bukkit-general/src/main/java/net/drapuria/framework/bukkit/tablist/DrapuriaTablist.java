package net.drapuria.framework.bukkit.tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.drapuria.framework.bukkit.reflection.version.PlayerVersion;
import net.drapuria.framework.bukkit.tablist.util.BufferedTabObject;
import net.drapuria.framework.bukkit.tablist.util.LegacyClientUtil;
import net.drapuria.framework.bukkit.tablist.util.TabColumn;
import net.drapuria.framework.bukkit.tablist.util.TabEntry;
import net.drapuria.framework.bukkit.util.CC;
import net.drapuria.framework.bukkit.util.Skin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marinus created on 23.06.2021 inside the package - de.vantrex.azure.tablist
 */

@Getter
public class DrapuriaTablist {

    private final Player player;

    private final Set<TabEntry> currentEntries = new HashSet<>();

    private boolean isSetup = true;

    private String header, footer;

    private final int slots = 80;

    public DrapuriaTablist(Player player) {
        this.player = player;
        final boolean version_1_7 = Minecraft.getProtocol(player) == PlayerVersion.v1_7;
        if (version_1_7)
            DrapuriaCommon.TASK_SCHEDULER.runAsyncScheduled(this::setup, 15);
        else
            setup();

    }

    private void setup() {
        final boolean version_1_7 = Minecraft.getProtocol(player) == PlayerVersion.v1_7;
        final int possibleSlots = version_1_7 ? 60 : slots;

        for (int i = 1; i <= possibleSlots; i++) {
            final TabColumn tabColumn = TabColumn.getFromSlot(player, i);
            if (tabColumn == null) {
                continue;
            }

            TabEntry tabEntry = DrapuriaTabHandler.getInstance().getImplementation().createFakePlayer(
                    this,
                    "0" + (i > 9 ? i : "0" + i) + "|Tab",
                    tabColumn,
                    tabColumn.getNumb(player, i),
                    i
            );
            if (version_1_7) {
                player.sendMessage("added team");

                Drapuria.IMPLEMENTATION.sendTeam(
                        player,
                        LegacyClientUtil.name(i - 1),
                        "",
                        "",
                        Collections.singleton(LegacyClientUtil.entry(i - 1)),
                        0
                );
            }
            currentEntries.add(tabEntry);
        }
        //   AzureTabHandler.getInstance().getImplementation().removeSelf(player);
        DrapuriaCommon.executorService.execute(() -> {
            update();
            isSetup = false;
        });
    }

    boolean destroying = false;

    public void update() {
        if (destroying)
            return;
        DrapuriaTabAdapter adapter = DrapuriaTabHandler.getInstance().getDefaultAdapter();

        final boolean version_1_7 = Minecraft.getProtocol(player) == PlayerVersion.v1_7;


        Set<TabEntry> previous = new HashSet<>(currentEntries);
        Set<BufferedTabObject> processedObjects = adapter.getSlots(player);
        if (processedObjects == null) {
            processedObjects = new HashSet<>();
        }

        for (BufferedTabObject scoreObject : processedObjects) {
            TabEntry tabEntry = getEntry(scoreObject.getColumn(), scoreObject.getSlot());
            if (tabEntry != null) {
                previous.remove(tabEntry);
                if (scoreObject.getPing() == null) {
                    DrapuriaTabHandler.getInstance().getImplementation().updateFakeLatency(this, tabEntry, 0);
                } else {
                    DrapuriaTabHandler.getInstance().getImplementation().updateFakeLatency(this, tabEntry, scoreObject.getPing());
                }

                if (!version_1_7) {
                    if (!tabEntry.getTexture().toString().equals(scoreObject.getSkin().toString())) {
                        DrapuriaTabHandler.getInstance().getImplementation().updateFakeSkin(this, tabEntry, scoreObject.getSkin());
                    }
                }
                DrapuriaTabHandler.getInstance().getImplementation().updateFakeName(this, tabEntry, scoreObject.getText());
            }
        }

        for (TabEntry tabEntry : previous) {
            DrapuriaTabHandler.getInstance().getImplementation().updateFakeName(this, tabEntry, "");
            DrapuriaTabHandler.getInstance().getImplementation().updateFakeLatency(this, tabEntry, 0);
            if (!version_1_7) {
                DrapuriaTabHandler.getInstance().getImplementation().updateFakeSkin(this, tabEntry, Skin.GRAY);
            }
        }

        previous.clear();

        String headerNow = CC.translate(adapter.getHeader(player));
        String footerNow = CC.translate(adapter.getFooter(player));

        if (!headerNow.equals(this.header) || !footerNow.equals(this.footer)) {
            DrapuriaTabHandler.getInstance().getImplementation().updateHeaderAndFooter(this, headerNow, footerNow);
            this.header = headerNow;
            this.footer = footerNow;
        }
    }

    public void destroy() {
        destroying = true;
        getCurrentEntries().forEach(entry -> DrapuriaTabHandler.getInstance().getImplementation().removeFakePlayer(this, entry));
        if (Minecraft.getProtocol(player) == PlayerVersion.v1_7)
            return;
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

    public TabEntry getEntry(TabColumn column, Integer slot) {
        for (TabEntry entry : currentEntries) {
            if (entry.getColumn().name().equalsIgnoreCase(column.name()) && entry.getSlot() == slot) {
                return entry;
            }
        }
        return null;
    }

    public static String[] splitStrings(String text, int rawSlot) {
        if (text.length() > 16) {
            String prefix = text.substring(0, 16);
            String suffix;

            if (prefix.charAt(15) == ChatColor.COLOR_CHAR || prefix.charAt(15) == '&') {
                prefix = prefix.substring(0, 15);
                suffix = text.substring(15);
            } else if (prefix.charAt(14) == ChatColor.COLOR_CHAR || prefix.charAt(14) == '&') {
                prefix = prefix.substring(0, 14);
                suffix = text.substring(14);
            } else {
                suffix = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', prefix)) + text.substring(16, text.length());
            }

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }

            //Bukkit.broadcastMessage(prefix + " |||| " + suffix);
            return new String[]{
                    prefix,
                    suffix
            };
        } else {
            return new String[]{
                    text
            };
        }
    }
}

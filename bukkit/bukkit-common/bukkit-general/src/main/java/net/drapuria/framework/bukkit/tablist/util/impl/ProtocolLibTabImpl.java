package net.drapuria.framework.bukkit.tablist.util.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import com.google.common.collect.ImmutableSet;
import net.drapuria.framework.bukkit.tablist.DrapuriaTabHandler;
import net.drapuria.framework.bukkit.tablist.DrapuriaTablist;
import net.drapuria.framework.bukkit.tablist.util.IDrapuriaTab;
import net.drapuria.framework.bukkit.tablist.util.TabColumn;
import net.drapuria.framework.bukkit.tablist.util.TabEntry;
import net.drapuria.framework.bukkit.util.Skin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

import java.util.Collections;
import java.util.UUID;

public class ProtocolLibTabImpl implements IDrapuriaTab {

    public ProtocolLibTabImpl() {
    }

    @Override
    public void registerLoginListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AzurePlugin.getInstance(), PacketType.Play.Server.LOGIN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                event.getPacket().getIntegers().write(2, 60);
            }
        });
    }

    @Override
    public void removeSelf(Player player) {
        WrappedPacketOutPlayerInfo packet = new WrappedPacketOutPlayerInfo(PlayerInfoAction.REMOVE_PLAYER, player);

        ImmutableSet.copyOf(Bukkit.getOnlinePlayers())
                .stream()
                .filter(online -> MinecraftReflection.getProtocol(online) == PlayerVersion.v1_7)
                .filter(online ->
                        Metadata.provideForPlayer(online).has(DrapuriaTabHandler.TABLIST_KEY))
                .forEach(online -> PacketService.send(online, packet));
    }

    @Override
    public TabEntry createFakePlayer(DrapuriaTablist tablist, String string, TabColumn column, Integer slot, Integer rawSlot) {
        UUID uuid = UUID.randomUUID();
        final Player player = tablist.getPlayer();
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        WrappedGameProfile profile = new WrappedGameProfile(uuid, string);
        PlayerInfoData playerInfoData = new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(""));

        playerInfoData.getProfile().getProperties().put("texture", new WrappedSignedProperty("textures", Skin.GRAY.skinValue, Skin.GRAY.skinSignature));

        packet.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
        sendPacket(player, packet);
        return new TabEntry(string, uuid, "", tablist, Skin.GRAY, column, slot, rawSlot, 0, profile);
    }

    @Override
    public void removeFakePlayer(DrapuriaTablist tablist, TabEntry entry) {
        final Player player = tablist.getPlayer();
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        WrappedGameProfile profile = new WrappedGameProfile(
                entry.getUuid(),
                entry.getId()
        );
        PlayerInfoData playerInfoData = new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(entry.getText()));
        packet.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
        sendPacket(player, packet);
    }

    @Override
    public void updateFakeName(DrapuriaTablist tablist, TabEntry tabEntry, String text) {
        if (tabEntry.getText().equals(text)) {
            return;
        }

        final Player player = tablist.getPlayer();

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        WrappedGameProfile profile = new WrappedGameProfile(
                tabEntry.getUuid(),
                tabEntry.getId()
        );
        PlayerInfoData playerInfoData = new PlayerInfoData(
                profile,
                1,
                EnumWrappers.NativeGameMode.SURVIVAL,
                //   WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', newStrings.length > 2 ? newStrings[0] + newStrings[1] + newStrings[2] : newStrings.length > 1 ? newStrings[0] + newStrings[1] : newStrings[0]))
                WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', text))
        );
        packet.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
        sendPacket(player, packet);

        tabEntry.setText(text);
    }

    @Override
    public void updateFakeLatency(DrapuriaTablist tablist, TabEntry tabEntry, Integer latency) {
        if (tabEntry.getLatency() == latency) {
            return;
        }

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY);

        WrappedGameProfile profile = new WrappedGameProfile(
                tabEntry.getUuid(),
                tabEntry.getId()
        );

        PlayerInfoData playerInfoData = new PlayerInfoData(
                profile,
                latency,
                EnumWrappers.NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', tabEntry.getText()))
        );

        packet.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
        sendPacket(tablist.getPlayer(), packet);
        tabEntry.setLatency(latency);
    }

    @Override
    public void updateFakeSkin(DrapuriaTablist tabList, TabEntry tabEntry, Skin skin) {
        if (tabEntry.getTexture() == skin) {
            return;
        }
        final Player player = tabList.getPlayer();

        WrappedGameProfile profile = new WrappedGameProfile(tabEntry.getUuid(), tabEntry.getId());
        PlayerInfoData playerInfoData = new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(""));

        playerInfoData.getProfile().getProperties().put("texture", new WrappedSignedProperty("textures", skin.skinValue, skin.skinSignature));

        PacketContainer remove = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        remove.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        remove.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));


        PacketContainer add = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        add.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        add.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));

        sendPacket(player, remove);
        sendPacket(player, add);

        tabEntry.setTexture(skin);
    }

    @Override
    public void updateHeaderAndFooter(DrapuriaTablist tablist, String header, String footer) {
        PacketContainer headerAndFooter = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        final Player player = tablist.getPlayer();
        headerAndFooter.getChatComponents().write(0, WrappedChatComponent.fromText(header));
        headerAndFooter.getChatComponents().write(1, WrappedChatComponent.fromText(footer));
        sendPacket(player, headerAndFooter);
    }

    private static void sendPacket(Player player, PacketContainer packetContainer) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

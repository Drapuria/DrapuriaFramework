/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol.packet.helper;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.papermc.paper.adventure.PaperAdventure;
import net.drapuria.framework.BootstrapInvoke;
import net.drapuria.framework.bukkit.protocol.ProtocolService;
import net.drapuria.framework.bukkit.protocol.packet.wrapper.server.WrappedPacketOutScoreboardTeam;
import net.drapuria.framework.bukkit.util.Skin;
import net.kyori.adventure.text.Component;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class VersionHelperImpl implements VersionHelper {

    @BootstrapInvoke
    public static void init() {
        ProtocolService.protocolService.setVersionHelper(new VersionHelperImpl());
    }

    @Override
    public Object getScoreboardTeamOptional(String name,
                                            String displayName,
                                            String prefix,
                                            String suffix,
                                            int chatFomat,
                                            int options,
                                            boolean friendlyFire,
                                            WrappedPacketOutScoreboardTeam.NameTagVisibility visibility) {
        ScoreboardTeam team = new ScoreboardTeam(new Scoreboard(), name);
        team.a(PaperAdventure.asVanilla(Component.text(displayName)));
        //    team.c(new ChatMessage(suffix));
        team.c(PaperAdventure.asVanilla(Component.text(suffix)));
        //  team.b(new ChatMessage(prefix));
        team.b(PaperAdventure.asVanilla(Component.text(prefix)));
        team.a(friendlyFire);
        team.b(visibility != WrappedPacketOutScoreboardTeam.NameTagVisibility.HIDE_FOR_OWN_TEAM);
        team.a(ScoreboardTeamBase.EnumNameTagVisibility.values()[visibility.ordinal()]);
        team.a(EnumChatFormat.a(chatFomat));
        return Optional.of(new PacketPlayOutScoreboardTeam.b(team));
    }

    @Override
    public Object getChatFormat(String str) {
        return new ChatMessage(str);
    }

    @Override
    public Skin getSkinFromPlayer(Player player) {
        GameProfile profile = ((CraftPlayer) player).getProfile();
        if (!profile.getProperties().get("textures").isEmpty()) {
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.getValue();
            String signature = property.getSignature();
            return new Skin(texture, signature);
        }
        return null;
    }
}

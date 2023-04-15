package net.drapuria.framework.bukkit.tablist.util;

import net.drapuria.framework.bukkit.tablist.DrapuriaTablist;
import net.drapuria.framework.bukkit.util.Skin;
import org.bukkit.entity.Player;

public interface IDrapuriaTab {

    default void removeSelf(Player player) {}

    default void removePlayers(Player player) {}

    void registerLoginListener();

    TabEntry createFakePlayer(DrapuriaTablist tabList, String string, TabColumn column, Integer slot, Integer rawSlot);

    void removeFakePlayer(DrapuriaTablist tablist, TabEntry entry);

    void updateFakeName(DrapuriaTablist tabList, TabEntry tabEntry, String text);

    void updateFakeLatency(DrapuriaTablist tabList, TabEntry tabEntry, Integer latency);

    void updateFakeSkin(DrapuriaTablist tabList, TabEntry tabEntry, Skin skin);

    void updateHeaderAndFooter(DrapuriaTablist tabList, String header, String footer);

}

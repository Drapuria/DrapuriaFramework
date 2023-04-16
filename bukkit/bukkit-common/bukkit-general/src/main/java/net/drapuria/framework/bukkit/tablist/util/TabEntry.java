package net.drapuria.framework.bukkit.tablist.util;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.tablist.DrapuriaTablist;
import net.drapuria.framework.bukkit.util.Skin;

import java.util.UUID;

@AllArgsConstructor
@Setter
@Getter
public class TabEntry {

    private String id;
    private UUID uuid;
    private String text;
    private DrapuriaTablist tablist;
    private Skin texture;
    private TabColumn column;
    private int slot;
    private int rawSlot;
    private int latency;
    private WrappedGameProfile profile;

}

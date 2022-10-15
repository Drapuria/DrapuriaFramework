package net.drapuria.framework.bukkit.fake.entity.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Setter;
import net.drapuria.framework.bukkit.fake.entity.FakeEntity;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityOptions;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class NPC extends FakeEntity {

    private static final Map<EnumWrappers.ItemSlot, Integer> SLOT_CONVERTER = new HashMap<>();

    static {
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.HEAD, 4);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.CHEST, 3);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.LEGS, 2);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.FEET, 1);
        SLOT_CONVERTER.put(EnumWrappers.ItemSlot.MAINHAND, 0);
    }

    private final NPCProfile npcProfile;
    @Setter
    private WrappedGameProfile gameProfile;
    private NameTagType nametagType;
    private SkinType skinType;

    public NPC(int entityId, FakeEntityOptions options, NPCProfile npcProfile) {
        super(entityId, options);
        this.npcProfile = npcProfile;
    }

    @Override
    public void show(Player player) {

    }

    @Override
    public void hide(Player player) {

    }

    @Override
    public void tickActionForPlayer(Player player) {

    }
}

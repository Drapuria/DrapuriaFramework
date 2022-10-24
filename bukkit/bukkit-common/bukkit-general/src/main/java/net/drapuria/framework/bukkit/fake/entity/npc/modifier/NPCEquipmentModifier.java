package net.drapuria.framework.bukkit.fake.entity.npc.modifier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.drapuria.framework.bukkit.fake.entity.modifier.FakeEntityModifier;
import net.drapuria.framework.bukkit.fake.entity.npc.NPC;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NPCEquipmentModifier extends FakeEntityModifier<NPC> {

    public NPCEquipmentModifier(@NotNull NPC fakeEntity) {
        super(fakeEntity);
    }

    public NPCEquipmentModifier queue(@NotNull EnumWrappers.ItemSlot itemSlot, @NotNull ItemStack equipment) {
        final PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packetContainer.getItemSlots().write(MINECRAFT_VERSION.olderThan(Minecraft.Version.v1_9_R1) ? 1 : 0, itemSlot);
        packetContainer.getItemModifier().write(0, equipment);
        return this;
    }

    public NPCEquipmentModifier queue(int itemSlot, @NotNull ItemStack equipment) {
        PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

        packetContainer.getIntegers().write(MINECRAFT_VERSION.olderThan(Minecraft.Version.v1_9_R1) ? 1 : 0, itemSlot);
        packetContainer.getItemModifier().write(0, equipment);

        return this;
    }

}

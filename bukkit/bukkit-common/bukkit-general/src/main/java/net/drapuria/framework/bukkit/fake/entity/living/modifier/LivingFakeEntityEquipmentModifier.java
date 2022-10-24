package net.drapuria.framework.bukkit.fake.entity.living.modifier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.drapuria.framework.bukkit.fake.entity.living.LivingFakeEntity;
import net.drapuria.framework.bukkit.fake.entity.modifier.FakeEntityModifier;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LivingFakeEntityEquipmentModifier extends FakeEntityModifier<LivingFakeEntity> {


    public LivingFakeEntityEquipmentModifier(@NotNull LivingFakeEntity fakeEntity) {
        super(fakeEntity);
    }


    public LivingFakeEntityEquipmentModifier queue(@NotNull EnumWrappers.ItemSlot itemSlot, @NotNull ItemStack equipment) {
        final PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packetContainer.getItemSlots().write(MINECRAFT_VERSION.olderThan(Minecraft.Version.v1_9_R1)  ? 1 : 0, itemSlot);
        packetContainer.getItemModifier().write(0, equipment);
        return this;
    }

    public LivingFakeEntityEquipmentModifier queue(int itemSlot, @NotNull ItemStack equipment) {
        final PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packetContainer.getIntegers().write(MINECRAFT_VERSION.olderThan(Minecraft.Version.v1_9_R1) ? 1 : 0, itemSlot);
        packetContainer.getItemModifier().write(0, equipment);
        return this;
    }
}

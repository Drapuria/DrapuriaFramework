package net.drapuria.framework.bukkit.fake.entity.npc.inventory;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.arcaniax.hdb.object.head.Head;
import net.drapuria.framework.bukkit.fake.entity.npc.NPC;
import net.drapuria.framework.bukkit.fake.entity.npc.modifier.NPCEquipmentModifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NPCInventory {

    private static final ItemStack NONE = new ItemStack(Material.AIR);

    private final transient NPC holder;

    private ItemStack[] armor = new ItemStack[4];
    private ItemStack[] contents = new ItemStack[36];
    private ItemStack offHand;

    public NPCInventory(NPC holder) {
        this.holder = holder;
    }

    private ItemStack getItemInHand() {
        return contents.length >= 1 ? contents[0] : null;
    }

    public void setItemInHand(final ItemStack itemStack) {
        contents[0] = itemStack;
        this.holder.equipmentModifier().queue(EnumWrappers.ItemSlot.MAINHAND, itemStack).send(this.holder.getSeeingPlayers());
    }

    public void setOffHand(ItemStack itemStack) { // 1.9+
        this.offHand = itemStack;
        this.holder.equipmentModifier().queue(EnumWrappers.ItemSlot.OFFHAND, itemStack).send(this.holder.getSeeingPlayers());
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack get(int slot) {
        return this.contents[slot];
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setHelmet(final ItemStack helmet) {
        this.armor[3] = helmet;
        this.holder.equipmentModifier().queue(EnumWrappers.ItemSlot.HEAD, helmet == null ? NONE : helmet)
                .send(this.holder.getSeeingPlayers());
    }

    public void setChestplate(final ItemStack chestplate) {
        this.armor[2] = chestplate;
        this.holder.equipmentModifier().queue(EnumWrappers.ItemSlot.CHEST, chestplate == null ? NONE : chestplate)
                .send(this.holder.getSeeingPlayers());
    }

    public void setLeggings(final ItemStack leggings) {
        this.armor[1] = leggings;
        this.holder.equipmentModifier().queue(EnumWrappers.ItemSlot.LEGS, leggings == null ? NONE : leggings)
                .send(this.holder.getSeeingPlayers());
    }

    public void setBoots(final ItemStack boots) {
        this.armor[1] = boots;
        this.holder.equipmentModifier().queue(EnumWrappers.ItemSlot.FEET, boots == null ? NONE : boots)
                .send(this.holder.getSeeingPlayers());
    }

    public void setContents(ItemStack[] contents) {
        if (contents == null)
            contents = new ItemStack[36];
        if (contents.length != 36)
            try {
                throw new Exception("Inventory has to be 36 Slots!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        this.contents = contents;
        final ItemStack itemStack = contents[0];
        this.holder.equipmentModifier().queue(EnumWrappers.ItemSlot.MAINHAND, itemStack == null ? NONE : itemStack)
                .send(this.holder.getSeeingPlayers());
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
        final NPCEquipmentModifier equipmentModifier = this.holder.equipmentModifier();
        for (int i = 0; i < armor.length; i++) {
            int slot = i + 1;
            final ItemStack itemStack = armor[i];
            equipmentModifier.queue(slot, itemStack == null ? NONE : itemStack);
        }
        equipmentModifier.send(this.holder.getSeeingPlayers());
    }

    public NPC getHolder() {
        return holder;
    }
}

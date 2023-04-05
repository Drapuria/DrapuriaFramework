/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.drapuria.framework.bukkit.item.meta.enchant.EnchantGlow;
import net.drapuria.framework.bukkit.util.ReflectionUtils;
import net.drapuria.framework.bukkit.util.Skin;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack itemStack;

    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, short data) {
        this.itemStack = new ItemStack(material, 1, data);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        this.itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        this.itemStack.setDurability((short) durability);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        this.itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder setGlow() {
        addEnchantment(EnchantGlow.getGlow(), 1);
        this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.itemMeta.setDisplayName(displayName == null ? null : ChatColor.translateAlternateColorCodes('&', displayName));
        return this;
    }

    public ItemBuilder setDisplayName(Component component) {
        this.itemMeta.displayName(component);
        return this;
    }

    public ItemBuilder setLoreComponents(List<Component> components) {
        this.itemMeta.lore(components);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        List<String> oldLore = this.itemMeta.hasLore() ? this.itemMeta.getLore() : new ArrayList<>();
        oldLore.addAll(lore);
        return setLore(oldLore);
    }

    public ItemBuilder setLore(String... lore) {
        final List<String> loreList = new ArrayList<>();
        for (String s : lore) {
            for (String parts : s.split("\n")) {
                loreList.add(parts);
            }
        }
        return setLore(loreList);
    }

    public ItemBuilder setLore(Component... lore) {
        final List<Component> loreList = new ArrayList<>(Arrays.asList(lore));
        return setLoreComponents(loreList);
    }

    public ItemBuilder setSkullOwner(String owner) {
        if (itemStack.getType() == Material.PLAYER_HEAD) {
            Skin skin = Skin.fromPlayer(owner);
            if (skin != null) {
                final SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                String displayName = null;
                if (meta.hasDisplayName()) {
                    displayName = meta.getDisplayName();
                }
                final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", skin.skinValue, skin.skinSignature));
                try {
                    ReflectionUtils.setValue(meta, meta.getClass(), true, "profile", profile);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                if (displayName != null) {
                    meta.setDisplayName(displayName);
                }
                this.itemMeta = meta;

            } else {
                SkullMeta meta = (SkullMeta) this.itemMeta;
                meta.setOwner(owner);
                this.itemMeta = meta;
            }
        }
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }


    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder of(Material material, short data) {
        return new ItemBuilder(material, data);
    }

    public static ItemBuilder of(Material material, int data) {
        return new ItemBuilder(material, (short) data);
    }

    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

}

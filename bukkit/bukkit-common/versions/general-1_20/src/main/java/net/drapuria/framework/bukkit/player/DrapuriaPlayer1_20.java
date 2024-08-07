/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.player;


import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.Title;
import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.entity.TargetEntityInfo;
import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.entity.TeleportFlag;
import io.papermc.paper.math.Position;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import net.drapuria.framework.language.LanguageService;
import net.drapuria.framework.language.message.AbstractLocalizedMessage;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.entity.Villager;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class DrapuriaPlayer1_20 implements DrapuriaPlayer {

    private static final LanguageService languageService = LanguageService.getService;
    private final Player player;
    private final long sessionJoin;
    private long lastTeleport;

    public DrapuriaPlayer1_20(Player player, long sessionJoin) {
        this.player = player;
        this.sessionJoin = sessionJoin;
    }

    @Override
    public @UnmodifiableView @NotNull Iterable<? extends BossBar> activeBossBars() {
        return player.activeBossBars();
    }

    @Override
    public @NotNull Component displayName() {
        return player.displayName();
    }

    @Override
    public void displayName(@Nullable Component component) {
        player.displayName(component);
    }

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public boolean isConnected() {
        return player.isConnected();
    }

    @Override
    public double getEyeHeight() {
        return player.getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean b) {
        return player.getEyeHeight(b);
    }

    @Override
    public @NotNull Location getEyeLocation() {
        return player.getEyeLocation();
    }

    @Override
    public @NotNull List<Block> getLineOfSight(@Nullable Set<Material> set, int i) {
        return player.getLineOfSight(set, i);
    }

    @Override
    public @NotNull Block getTargetBlock(@Nullable Set<Material> set, int i) {
        return player.getTargetBlock(set, i);
    }

    @Override
    public @Nullable Block getTargetBlock(int i, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        return player.getTargetBlock(i, fluidMode);
    }

    @Override
    public @Nullable BlockFace getTargetBlockFace(int i, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        return player.getTargetBlockFace(i, fluidMode);
    }

    @Override
    public @Nullable BlockFace getTargetBlockFace(int i, @NotNull FluidCollisionMode fluidCollisionMode) {
        return player.getTargetBlockFace(i, fluidCollisionMode);
    }

    @Override
    public @Nullable TargetBlockInfo getTargetBlockInfo(int i, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        return player.getTargetBlockInfo(i, fluidMode);
    }

    @Override
    public @Nullable Entity getTargetEntity(int i, boolean b) {
        return player.getTargetEntity(i, b);
    }

    @Override
    public @Nullable TargetEntityInfo getTargetEntityInfo(int i, boolean b) {
        return player.getTargetEntityInfo(i, b);
    }

    @Override
    public @Nullable RayTraceResult rayTraceEntities(int i, boolean b) {
        return player.rayTraceEntities(i, b);
    }

    @Override
    public @NotNull List<Block> getLastTwoTargetBlocks(@Nullable Set<Material> set, int i) {
        return player.getLastTwoTargetBlocks(set, i);
    }

    @Override
    public @Nullable Block getTargetBlockExact(int i) {
        return player.getTargetBlockExact(i);
    }

    @Override
    public @Nullable Block getTargetBlockExact(int i, @NotNull FluidCollisionMode fluidCollisionMode) {
        return player.getTargetBlockExact(i, fluidCollisionMode);
    }

    @Override
    public @Nullable RayTraceResult rayTraceBlocks(double v) {
        return player.rayTraceBlocks(v);
    }

    @Override
    public @Nullable RayTraceResult rayTraceBlocks(double v, @NotNull FluidCollisionMode fluidCollisionMode) {
        return player.rayTraceBlocks(v, fluidCollisionMode);
    }

    @Override
    public int getRemainingAir() {
        return player.getRemainingAir();
    }

    @Override
    public void setRemainingAir(int i) {
        player.setRemainingAir(i);
    }

    @Override
    public int getMaximumAir() {
        return player.getMaximumAir();
    }

    @Override
    public void setMaximumAir(int i) {
        player.setMaximumAir(i);
    }

    @Override
    public int getArrowCooldown() {
        return player.getArrowCooldown();
    }

    @Override
    public void setArrowCooldown(int i) {
        player.setArrowCooldown(i);
    }

    @Override
    public int getArrowsInBody() {
        return player.getArrowsInBody();
    }

    @Override
    public void setArrowsInBody(int i, boolean b) {
        player.setArrowsInBody(i, b);
    }

    @Override
    public int getBeeStingerCooldown() {
        return player.getBeeStingerCooldown();
    }

    @Override
    public void setBeeStingerCooldown(int i) {
        player.setBeeStingerCooldown(i);
    }

    @Override
    public int getBeeStingersInBody() {
        return player.getBeeStingersInBody();
    }

    @Override
    public void setBeeStingersInBody(int i) {
        player.setBeeStingersInBody(i);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return player.getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {
        player.setMaximumNoDamageTicks(i);
    }

    @Override
    public double getLastDamage() {
        return player.getLastDamage();
    }

    @Override
    public void setLastDamage(double v) {
        player.setLastDamage(v);
    }

    @Override
    public int getNoDamageTicks() {
        return player.getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int i) {
        player.setNoDamageTicks(i);
    }

    @Override
    public int getNoActionTicks() {
        return player.getNoActionTicks();
    }

    @Override
    public void setNoActionTicks(int i) {
        player.setNoActionTicks(i);
    }

    @Override
    public @Nullable Player getKiller() {
        return player.getKiller();
    }

    @Override
    public void setKiller(@Nullable Player player) {
        player.setKiller(player);
    }

    @Override
    public boolean addPotionEffect(@NotNull PotionEffect potionEffect) {
        return player.addPotionEffect(potionEffect);
    }

    @Override
    public boolean addPotionEffect(@NotNull PotionEffect potionEffect, boolean b) {
        return player.addPotionEffect(potionEffect, b);
    }

    @Override
    public boolean addPotionEffects(@NotNull Collection<PotionEffect> collection) {
        return player.addPotionEffects(collection);
    }

    @Override
    public boolean hasPotionEffect(@NotNull PotionEffectType potionEffectType) {
        return player.hasPotionEffect(potionEffectType);
    }

    @Override
    public @Nullable PotionEffect getPotionEffect(@NotNull PotionEffectType potionEffectType) {
        return player.getPotionEffect(potionEffectType);
    }

    @Override
    public void removePotionEffect(@NotNull PotionEffectType potionEffectType) {
        player.removePotionEffect(potionEffectType);
    }

    @Override
    public @NotNull Collection<PotionEffect> getActivePotionEffects() {
        return player.getActivePotionEffects();
    }

    @Override
    public boolean clearActivePotionEffects() {
        return player.clearActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(@NotNull Entity entity) {
        return player.hasLineOfSight(entity);
    }

    @Override
    public boolean hasLineOfSight(@NotNull Location location) {
        return player.hasLineOfSight(location);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return player.getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean b) {
        player.setRemoveWhenFarAway(b);
    }

    @Override
    public @NotNull EntityEquipment getEquipment() {
        return player.getEquipment();
    }

    @Override
    public void setCanPickupItems(boolean b) {
        player.setCanPickupItems(b);
    }

    @Override
    public boolean getCanPickupItems() {
        return player.getCanPickupItems();
    }

    @Override
    public boolean isLeashed() {
        return player.isLeashed();
    }

    @Override
    public @NotNull Entity getLeashHolder() throws IllegalStateException {
        return player.getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(@Nullable Entity entity) {
        return player.setLeashHolder(entity);
    }

    @Override
    public boolean isGliding() {
        return player.isGliding();
    }

    @Override
    public void setGliding(boolean b) {
        player.setGliding(b);
    }

    @Override
    public boolean isSwimming() {
        return player.isSwimming();
    }

    @Override
    public void setSwimming(boolean b) {
        player.setSwimming(b);
    }

    @Override
    public boolean isRiptiding() {
        return player.isRiptiding();
    }

    @Override
    public boolean isSleeping() {
        return player.isSleeping();
    }

    @Override
    public boolean isClimbing() {
        return player.isClimbing();
    }

    @Override
    public void setAI(boolean b) {
        player.setAI(b);
    }

    @Override
    public boolean hasAI() {
        return player.hasAI();
    }

    @Override
    public void attack(@NotNull Entity entity) {
        player.attack(entity);
    }

    @Override
    public void swingMainHand() {
        player.swingMainHand();
    }

    @Override
    public void swingOffHand() {
        player.swingOffHand();
    }

    @Override
    public void playHurtAnimation(float v) {
        player.playHurtAnimation(v);
    }

    @Override
    public void setCollidable(boolean b) {
        player.setCollidable(b);
    }

    @Override
    public boolean isCollidable() {
        return player.isCollidable();
    }

    @Override
    public @NotNull Set<UUID> getCollidableExemptions() {
        return player.getCollidableExemptions();
    }

    @Override
    public <T> @Nullable T getMemory(@NotNull MemoryKey<T> memoryKey) {
        return player.getMemory(memoryKey);
    }

    @Override
    public <T> void setMemory(@NotNull MemoryKey<T> memoryKey, @Nullable T t) {
        player.setMemory(memoryKey, t);
    }

    @Override
    public @Nullable Sound getHurtSound() {
        return player.getHurtSound();
    }

    @Override
    public @Nullable Sound getDeathSound() {
        return player.getDeathSound();
    }

    @Override
    public @NotNull Sound getFallDamageSound(int i) {
        return player.getFallDamageSound(i);
    }

    @Override
    public @NotNull Sound getFallDamageSoundSmall() {
        return player.getFallDamageSoundSmall();
    }

    @Override
    public @NotNull Sound getFallDamageSoundBig() {
        return player.getFallDamageSoundBig();
    }

    @Override
    public @NotNull Sound getDrinkingSound(@NotNull ItemStack itemStack) {
        return player.getDrinkingSound(itemStack);
    }

    @Override
    public @NotNull Sound getEatingSound(@NotNull ItemStack itemStack) {
        return player.getEatingSound(itemStack);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return player.canBreatheUnderwater();
    }

    @Override
    public @NotNull EntityCategory getCategory() {
        return player.getCategory();
    }

    @Override
    public void setInvisible(boolean b) {
        player.setInvisible(b);
    }

    @Override
    public boolean isInvisible() {
        return player.isInvisible();
    }

    @Override
    public int getArrowsStuck() {
        return player.getArrowsStuck();
    }

    @Override
    public void setArrowsStuck(int i) {
        player.setArrowsStuck(i);
    }

    @Override
    public int getShieldBlockingDelay() {
        return player.getShieldBlockingDelay();
    }

    @Override
    public void setShieldBlockingDelay(int i) {
        player.setShieldBlockingDelay(i);
    }

    @Override
    public @NotNull ItemStack getActiveItem() {
        return player.getActiveItem();
    }

    @Override
    public void clearActiveItem() {
        player.clearActiveItem();
    }

    @Override
    public float getSidewaysMovement() {
        return player.getSidewaysMovement();
    }

    @Override
    public float getUpwardsMovement() {
        return player.getUpwardsMovement();
    }

    @Override
    public float getForwardsMovement() {
        return player.getForwardsMovement();
    }

    @Override
    public int getItemUseRemainingTime() {
        return player.getItemUseRemainingTime();
    }

    @Override
    public int getHandRaisedTime() {
        return player.getHandRaisedTime();
    }

    @Override
    public @NotNull String getName() {
        return player.getName();
    }

    @Override
    public @NotNull PlayerInventory getInventory() {
        return player.getInventory();
    }

    @Override
    public @NotNull Inventory getEnderChest() {
        return player.getEnderChest();
    }

    @Override
    public @NotNull MainHand getMainHand() {
        return player.getMainHand();
    }

    @Override
    public boolean setWindowProperty(@NotNull InventoryView.Property property, int i) {
        return player.setWindowProperty(property, i);
    }

    @Override
    public int getEnchantmentSeed() {
        return player.getEnchantmentSeed();
    }

    @Override
    public void setEnchantmentSeed(int i) {
        player.setEnchantmentSeed(i);
    }

    @Override
    public @NotNull InventoryView getOpenInventory() {
        return player.getOpenInventory();
    }

    @Override
    public @Nullable InventoryView openInventory(@NotNull Inventory inventory) {
        return player.openInventory(inventory);
    }

    @Override
    public @Nullable InventoryView openWorkbench(@Nullable Location location, boolean b) {
        return player.openWorkbench(location, b);
    }

    @Override
    public @Nullable InventoryView openEnchanting(@Nullable Location location, boolean b) {
        return player.openEnchanting(location, b);
    }

    @Override
    public void openInventory(@NotNull InventoryView inventoryView) {
        player.openInventory(inventoryView);
    }

    @Override
    public @Nullable InventoryView openMerchant(@NotNull Villager villager, boolean b) {
        return player.openMerchant(villager, b);
    }

    @Override
    public @Nullable InventoryView openMerchant(@NotNull Merchant merchant, boolean b) {
        return player.openMerchant(merchant, b);
    }

    @Override
    public @Nullable InventoryView openAnvil(@Nullable Location location, boolean b) {
        return player.openAnvil(location, b);
    }

    @Override
    public @Nullable InventoryView openCartographyTable(@Nullable Location location, boolean b) {
        return player.openCartographyTable(location, b);
    }

    @Override
    public @Nullable InventoryView openGrindstone(@Nullable Location location, boolean b) {
        return player.openGrindstone(location, b);
    }

    @Override
    public @Nullable InventoryView openLoom(@Nullable Location location, boolean b) {
        return player.openLoom(location, b);
    }

    @Override
    public @Nullable InventoryView openSmithingTable(@Nullable Location location, boolean b) {
        return player.openSmithingTable(location, b);
    }

    @Override
    public @Nullable InventoryView openStonecutter(@Nullable Location location, boolean b) {
        return player.openStonecutter(location, b);
    }

    @Override
    public void closeInventory() {
        player.closeInventory();
    }

    @Override
    public void closeInventory(@NotNull InventoryCloseEvent.Reason reason) {
        player.closeInventory(reason);
    }

    @Override
    public @NotNull ItemStack getItemInHand() {
        return player.getItemInHand();
    }

    @Override
    public void setItemInHand(@Nullable ItemStack itemStack) {
        player.setItemInHand(itemStack);
    }

    @Override
    public @NotNull ItemStack getItemOnCursor() {
        return player.getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(@Nullable ItemStack itemStack) {
        player.setItemOnCursor(itemStack);
    }

    @Override
    public boolean hasCooldown(@NotNull Material material) {
        return player.hasCooldown(material);
    }

    @Override
    public int getCooldown(@NotNull Material material) {
        return player.getCooldown(material);
    }

    @Override
    public void setCooldown(@NotNull Material material, int i) {
        player.setCooldown(material, i);
    }

    @Override
    public void setHurtDirection(float v) {
        player.setHurtDirection(v);
    }

    @Override
    public void knockback(double v, double v1, double v2) {
        player.knockback(v, v1, v2);
    }

    @Override
    public void broadcastSlotBreak(@NotNull EquipmentSlot equipmentSlot) {
        player.broadcastSlotBreak(equipmentSlot);
    }

    @Override
    public void broadcastSlotBreak(@NotNull EquipmentSlot equipmentSlot, @NotNull Collection<Player> collection) {
        player.broadcastSlotBreak(equipmentSlot, collection);
    }

    @Override
    public @NotNull ItemStack damageItemStack(@NotNull ItemStack itemStack, int i) {
        return player.damageItemStack(itemStack, i);
    }

    @Override
    public void damageItemStack(@NotNull EquipmentSlot equipmentSlot, int i) {
        player.damageItemStack(equipmentSlot, i);
    }

    @Override
    public float getBodyYaw() {
        return player.getBodyYaw();
    }

    @Override
    public void setBodyYaw(float v) {
        player.setBodyYaw(v);
    }

    @Override
    public boolean isDeeplySleeping() {
        return player.isDeeplySleeping();
    }

    @Override
    public int getSleepTicks() {
        return player.getSleepTicks();
    }

    @Override
    public @Nullable Location getPotentialBedLocation() {
        return player.getPotentialBedLocation();
    }

    @Override
    public @Nullable FishHook getFishHook() {
        return player.getFishHook();
    }

    @Override
    public boolean sleep(@NotNull Location location, boolean b) {
        return player.sleep(location, b);
    }

    @Override
    public void wakeup(boolean b) {
        player.wakeup(b);
    }

    @Override
    public @NotNull Location getBedLocation() {
        return player.getBedLocation();
    }

    @Override
    public @NotNull GameMode getGameMode() {
        return player.getGameMode();
    }

    @Override
    public void setGameMode(@NotNull GameMode gameMode) {
        player.setGameMode(gameMode);
    }

    @Override
    public boolean isBlocking() {
        return player.isBlocking();
    }

    @Override
    public boolean isHandRaised() {
        return player.isHandRaised();
    }

    @Override
    public @NotNull EquipmentSlot getHandRaised() {
        return player.getHandRaised();
    }

    @Override
    public boolean isJumping() {
        return player.isJumping();
    }

    @Override
    public void setJumping(boolean b) {
        player.setJumping(b);
    }

    @Override
    public void playPickupItemAnimation(@NotNull Item item, int i) {
        player.playPickupItemAnimation(item, i);
    }

    @Override
    public float getHurtDirection() {
        return player.getHurtDirection();
    }

    @Override
    public @Nullable ItemStack getItemInUse() {
        return player.getItemInUse();
    }

    @Override
    public int getExpToLevel() {
        return player.getExpToLevel();
    }

    @Override
    public @Nullable Entity releaseLeftShoulderEntity() {
        return player.releaseLeftShoulderEntity();
    }

    @Override
    public @Nullable Entity releaseRightShoulderEntity() {
        return player.releaseRightShoulderEntity();
    }

    @Override
    public float getAttackCooldown() {
        return player.getAttackCooldown();
    }

    @Override
    public boolean discoverRecipe(@NotNull NamespacedKey namespacedKey) {
        return player.discoverRecipe(namespacedKey);
    }

    @Override
    public int discoverRecipes(@NotNull Collection<NamespacedKey> collection) {
        return player.discoverRecipes(collection);
    }

    @Override
    public boolean undiscoverRecipe(@NotNull NamespacedKey namespacedKey) {
        return player.undiscoverRecipe(namespacedKey);
    }

    @Override
    public int undiscoverRecipes(@NotNull Collection<NamespacedKey> collection) {
        return player.undiscoverRecipes(collection);
    }

    @Override
    public boolean hasDiscoveredRecipe(@NotNull NamespacedKey namespacedKey) {
        return player.hasDiscoveredRecipe(namespacedKey);
    }

    @Override
    public @NotNull Set<NamespacedKey> getDiscoveredRecipes() {
        return player.getDiscoveredRecipes();
    }

    @Override
    public @Nullable Entity getShoulderEntityLeft() {
        return player.getShoulderEntityLeft();
    }

    @Override
    public void setShoulderEntityLeft(@Nullable Entity entity) {
        player.setShoulderEntityLeft(entity);
    }

    @Override
    public @Nullable Entity getShoulderEntityRight() {
        return player.getShoulderEntityRight();
    }

    @Override
    public void setShoulderEntityRight(@Nullable Entity entity) {
        player.setShoulderEntityRight(entity);
    }

    @Override
    public @NotNull String getDisplayName() {
        return player.getDisplayName();
    }

    @Override
    public void setDisplayName(@Nullable String s) {
        player.setDisplayName(s);
    }

    @Override
    public void playerListName(@Nullable Component component) {
        player.playerListName(component);
    }

    @Override
    public @NotNull Component playerListName() {
        return player.playerListName();
    }

    @Override
    public @Nullable Component playerListHeader() {
        return player.playerListHeader();
    }

    @Override
    public @Nullable Component playerListFooter() {
        return player.playerListFooter();
    }

    @Override
    public @NotNull String getPlayerListName() {
        return player.getPlayerListName();
    }

    @Override
    public void setPlayerListName(@Nullable String s) {
        player.setPlayerListName(s);
    }

    @Override
    public @Nullable String getPlayerListHeader() {
        return player.getPlayerListHeader();
    }

    @Override
    public @Nullable String getPlayerListFooter() {
        return player.getPlayerListFooter();
    }

    @Override
    public void setPlayerListHeader(@Nullable String s) {
        player.setPlayerListHeader(s);
    }

    @Override
    public void setPlayerListFooter(@Nullable String s) {
        player.setPlayerListFooter(s);
    }

    @Override
    public void setPlayerListHeaderFooter(@Nullable String s, @Nullable String s1) {
        player.setPlayerListHeaderFooter(s, s1);
    }

    @Override
    public void setCompassTarget(@NotNull Location location) {
        player.setCompassTarget(location);
    }

    @Override
    public @NotNull Location getCompassTarget() {
        return player.getCompassTarget();
    }

    @Override
    public @Nullable InetSocketAddress getAddress() {
        return player.getAddress();
    }

    @Override
    public int getProtocolVersion() {
        return player.getProtocolVersion();
    }

    @Override
    public @Nullable InetSocketAddress getVirtualHost() {
        return player.getVirtualHost();
    }

    @Override
    public boolean isConversing() {
        return player.isConversing();
    }

    @Override
    public void acceptConversationInput(@NotNull String s) {
        player.acceptConversationInput(s);
    }

    @Override
    public boolean beginConversation(@NotNull Conversation conversation) {
        return player.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation) {
        player.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent conversationAbandonedEvent) {
        player.abandonConversation(conversation, conversationAbandonedEvent);
    }

    @Override
    public void sendRawMessage(@NotNull String s) {
        player.sendRawMessage(s);
    }

    @Override
    public void sendRawMessage(@Nullable UUID uuid, @NotNull String s) {
        player.sendRawMessage(uuid, s);
    }

    @Override
    public void kickPlayer(@Nullable String s) {
        player.kickPlayer(s);
    }

    @Override
    public void kick() {
        player.kick();
    }

    @Override
    public void kick(@Nullable Component component) {
        player.kick(component);
    }

    @Override
    public void kick(@Nullable Component component, PlayerKickEvent.Cause cause) {
        player.kick(component, cause);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Date date, @Nullable String s1, boolean b) {
        return player.ban(s, date, s1, b);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Instant instant, @Nullable String s1, boolean b) {
        return player.ban(s, instant, s1, b);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Duration duration, @Nullable String s1, boolean b) {
        return player.ban(s, duration, s1, b);
    }

    @Override
    public @Nullable BanEntry<InetAddress> banIp(@Nullable String s, @Nullable Date date, @Nullable String s1, boolean b) {
        return player.banIp(s, date, s1, b);
    }

    @Override
    public @Nullable BanEntry<InetAddress> banIp(@Nullable String s, @Nullable Instant instant, @Nullable String s1, boolean b) {
        return player.banIp(s, instant, s1, b);
    }

    @Override
    public @Nullable BanEntry<InetAddress> banIp(@Nullable String s, @Nullable Duration duration, @Nullable String s1, boolean b) {
        return player.banIp(s, duration, s1, b);
    }

    @Override
    public void chat(@NotNull String s) {
        player.chat(s);
    }

    @Override
    public boolean performCommand(@NotNull String s) {
        return player.performCommand(s);
    }

    @Override
    public @NotNull Location getLocation() {
        return player.getLocation();
    }

    @Override
    public @Nullable Location getLocation(@Nullable Location location) {
        return player.getLocation(location);
    }

    @Override
    public void setVelocity(@NotNull Vector vector) {
        player.setVelocity(vector);
    }

    @Override
    public @NotNull Vector getVelocity() {
        return player.getVelocity();
    }

    @Override
    public double getHeight() {
        return player.getHeight();
    }

    @Override
    public double getWidth() {
        return player.getWidth();
    }

    @Override
    public @NotNull BoundingBox getBoundingBox() {
        return player.getBoundingBox();
    }

    @Override
    public boolean isOnGround() {
        return player.isOnGround();
    }

    @Override
    public boolean isInWater() {
        return player.isInWater();
    }

    @Override
    public @NotNull World getWorld() {
        return player.getWorld();
    }

    @Override
    public boolean isSneaking() {
        return player.isSneaking();
    }

    @Override
    public void setSneaking(boolean b) {
        player.setSneaking(b);
    }

    @Override
    public void setPose(@NotNull Pose pose, boolean b) {
        player.setPose(pose, b);
    }

    @Override
    public boolean hasFixedPose() {
        return player.hasFixedPose();
    }

    @Override
    public @NotNull SpawnCategory getSpawnCategory() {
        return player.getSpawnCategory();
    }

    @Override
    public boolean isSprinting() {
        return player.isSprinting();
    }

    @Override
    public void setSprinting(boolean b) {
        player.setSprinting(b);
    }

    @Override
    public void saveData() {
        player.saveData();
    }

    @Override
    public void loadData() {
        player.loadData();
    }

    @Override
    public void setSleepingIgnored(boolean b) {
        player.setSleepingIgnored(b);
    }

    @Override
    public boolean isSleepingIgnored() {
        return player.isSleepingIgnored();
    }

    @Override
    public @Nullable Location getBedSpawnLocation() {
        return player.getBedSpawnLocation();
    }

    @Override
    public long getLastLogin() {
        return player.getLastLogin();
    }

    @Override
    public long getLastSeen() {
        return player.getLastSeen();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        player.incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        player.decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, int i) throws IllegalArgumentException {
        player.incrementStatistic(statistic, i);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, int i) throws IllegalArgumentException {
        player.decrementStatistic(statistic, i);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, int i) throws IllegalArgumentException {
        player.setStatistic(statistic, i);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        return player.getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        player.incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        player.decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        return player.getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int i) throws IllegalArgumentException {
        player.incrementStatistic(statistic, material, i);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int i) throws IllegalArgumentException {
        player.decrementStatistic(statistic, material, i);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull Material material, int i) throws IllegalArgumentException {
        player.setStatistic(statistic, material, i);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        player.incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        player.decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        return player.getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int i) throws IllegalArgumentException {
        player.incrementStatistic(statistic, entityType, i);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int i) {
        player.decrementStatistic(statistic, entityType, i);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int i) {
        player.setStatistic(statistic, entityType, i);
    }

    @Override
    public void setBedSpawnLocation(@Nullable Location location) {
        player.setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(@Nullable Location location, boolean b) {
        player.setBedSpawnLocation(location, b);
    }

    @Override
    public void playNote(@NotNull Location location, byte b, byte b1) {
        player.playNote(location, b, b1);
    }

    @Override
    public void playNote(@NotNull Location location, @NotNull Instrument instrument, @NotNull Note note) {
        player.playNote(location, instrument, note);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, float v, float v1) {
        player.playSound(location, sound, v, v1);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String s, float v, float v1) {
        player.playSound(location, s, v, v1);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, @NotNull SoundCategory soundCategory, float v, float v1) {
        player.playSound(location, sound, soundCategory, v, v1);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String s, @NotNull SoundCategory soundCategory, float v, float v1) {
        player.playSound(location, s, soundCategory, v, v1);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, @NotNull SoundCategory soundCategory, float v, float v1, long l) {
        player.playSound(location, sound, soundCategory, v, v1, l);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String s, @NotNull SoundCategory soundCategory, float v, float v1, long l) {
        player.playSound(location, s, soundCategory, v, v1, l);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, float v, float v1) {
        player.playSound(entity, sound, v, v1);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull String s, float v, float v1) {
        player.playSound(entity, s, v, v1);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, @NotNull SoundCategory soundCategory, float v, float v1) {
        player.playSound(entity, sound, soundCategory, v, v1);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull String s, @NotNull SoundCategory soundCategory, float v, float v1) {
        player.playSound(entity, s, soundCategory, v, v1);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, @NotNull SoundCategory soundCategory, float v, float v1, long l) {
        player.playSound(entity, sound, soundCategory, v, v1, l);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull String s, @NotNull SoundCategory soundCategory, float v, float v1, long l) {
        player.playSound(entity, s, soundCategory, v, v1, l);
    }

    @Override
    public void stopSound(@NotNull Sound sound) {
        player.stopSound(sound);
    }

    @Override
    public void stopSound(@NotNull String s) {
        player.stopSound(s);
    }

    @Override
    public void stopSound(@NotNull Sound sound, @Nullable SoundCategory soundCategory) {
        player.stopSound(sound, soundCategory);
    }

    @Override
    public void stopSound(@NotNull String s, @Nullable SoundCategory soundCategory) {
        player.stopSound(s, soundCategory);
    }

    @Override
    public void stopSound(@NotNull SoundCategory soundCategory) {
        player.stopSound(soundCategory);
    }

    @Override
    public void stopAllSounds() {
        player.stopAllSounds();
    }

    @Override
    public void playEffect(@NotNull Location location, @NotNull Effect effect, int i) {
        player.playEffect(location, effect, i);
    }

    @Override
    public <T> void playEffect(@NotNull Location location, @NotNull Effect effect, @Nullable T t) {
        player.playEffect(location, effect, t);
    }

    @Override
    public boolean breakBlock(@NotNull Block block) {
        return player.breakBlock(block);
    }

    @Override
    public void sendBlockChange(@NotNull Location location, @NotNull Material material, byte b) {
        player.sendBlockChange(location, material, b);
    }

    @Override
    public void sendBlockChange(@NotNull Location location, @NotNull BlockData blockData) {
        player.sendBlockChange(location, blockData);
    }

    @Override
    public void sendBlockChanges(@NotNull Collection<BlockState> collection) {
        player.sendBlockChanges(collection);
    }

    @Override
    public void sendBlockChanges(@NotNull Collection<BlockState> collection, boolean b) {
        player.sendBlockChanges(collection, b);
    }

    @Override
    public void sendBlockDamage(@NotNull Location location, float v) {
        player.sendBlockDamage(location, v);
    }

    @Override
    public void sendMultiBlockChange(@NotNull Map<? extends Position, BlockData> map) {
        player.sendMultiBlockChange(map);
    }

    @Override
    public void sendBlockDamage(@NotNull Location location, float v, @NotNull Entity entity) {
        player.sendBlockDamage(location, v, entity);
    }

    @Override
    public void sendBlockDamage(@NotNull Location location, float v, int i) {
        player.sendBlockDamage(location, v, i);
    }

    @Override
    public void sendEquipmentChange(@NotNull LivingEntity livingEntity, @NotNull EquipmentSlot equipmentSlot, @Nullable ItemStack itemStack) {
        player.sendEquipmentChange(livingEntity, equipmentSlot, itemStack);
    }

    @Override
    public void sendEquipmentChange(@NotNull LivingEntity livingEntity, @NotNull Map<EquipmentSlot, ItemStack> map) {
        player.sendEquipmentChange(livingEntity, map);
    }

    @Override
    public void sendSignChange(@NotNull Location location, @Nullable List<? extends Component> list, @NotNull DyeColor dyeColor, boolean b) throws IllegalArgumentException {
        player.sendSignChange(location, list, dyeColor, b);
    }

    @Override
    public void sendSignChange(@NotNull Location location, @Nullable String[] strings) throws IllegalArgumentException {
        player.sendSignChange(location, strings);
    }

    @Override
    public void sendSignChange(@NotNull Location location, @Nullable String[] strings, @NotNull DyeColor dyeColor) throws IllegalArgumentException {
        player.sendSignChange(location, strings, dyeColor);
    }

    @Override
    public void sendSignChange(@NotNull Location location, @Nullable String[] strings, @NotNull DyeColor dyeColor, boolean b) throws IllegalArgumentException {
        player.sendSignChange(location, strings, dyeColor, b);
    }

    @Override
    public void sendBlockUpdate(@NotNull Location location, @NotNull TileState tileState) throws IllegalArgumentException {
        player.sendBlockUpdate(location, tileState);
    }

    @Override
    public void sendMap(@NotNull MapView mapView) {
        player.sendMap(mapView);
    }

    @Override
    public void showWinScreen() {
        player.showWinScreen();
    }

    @Override
    public boolean hasSeenWinScreen() {
        return player.hasSeenWinScreen();
    }

    @Override
    public void setHasSeenWinScreen(boolean b) {
        player.setHasSeenWinScreen(b);
    }

    @Override
    public void sendActionBar(String text) {
        player.sendActionBar(text);
    }

    @Override
    public void sendActionBar(char c, @NotNull String s) {
        player.sendActionBar(c, s);
    }

    @Override
    public void sendActionBar(@NotNull BaseComponent... baseComponents) {
        player.sendActionBar(baseComponents);
    }

    @Override
    public void setPlayerListHeaderFooter(@Nullable BaseComponent[] baseComponents, @Nullable BaseComponent[] baseComponents1) {
        player.setPlayerListHeaderFooter(baseComponents, baseComponents1);
    }

    @Override
    public void setPlayerListHeaderFooter(@Nullable BaseComponent baseComponent, @Nullable BaseComponent baseComponent1) {
        player.setPlayerListHeaderFooter(baseComponent, baseComponent1);
    }

    @Override
    public void setTitleTimes(int i, int i1, int i2) {
        player.setTitleTimes(i, i1, i2);
    }

    @Override
    public void setSubtitle(BaseComponent[] baseComponents) {
        player.setSubtitle(baseComponents);
    }

    @Override
    public void setSubtitle(BaseComponent baseComponent) {
        player.setSubtitle(baseComponent);
    }

    @Override
    public void showTitle(@Nullable BaseComponent[] baseComponents) {
        player.showTitle(baseComponents);
    }

    @Override
    public void showTitle(@Nullable BaseComponent baseComponent) {
        player.showTitle(baseComponent);
    }

    @Override
    public void showTitle(@Nullable BaseComponent[] baseComponents, @Nullable BaseComponent[] baseComponents1, int i, int i1, int i2) {
        player.showTitle(baseComponents, baseComponents1, i, i1, i2);
    }

    @Override
    public void showTitle(@Nullable BaseComponent baseComponent, @Nullable BaseComponent baseComponent1, int i, int i1, int i2) {
        player.showTitle(baseComponent, baseComponent1, i, i1, i2);
    }

    @Override
    public void sendTitle(@NotNull Title title) {
        player.sendTitle(title);
    }

    @Override
    public void updateTitle(@NotNull Title title) {
        player.updateTitle(title);
    }

    @Override
    public void hideTitle() {
        player.hideTitle();
    }

    @Override
    public void sendHurtAnimation(float v) {
        player.sendHurtAnimation(v);
    }

    @Override
    public void addCustomChatCompletions(@NotNull Collection<String> collection) {
        player.addCustomChatCompletions(collection);
    }

    @Override
    public void removeCustomChatCompletions(@NotNull Collection<String> collection) {
        player.removeCustomChatCompletions(collection);
    }

    @Override
    public void setCustomChatCompletions(@NotNull Collection<String> collection) {
        player.setCustomChatCompletions(collection);
    }

    @Override
    public void updateInventory() {
        player.updateInventory();
    }

    @Override
    public @Nullable GameMode getPreviousGameMode() {
        return player.getPreviousGameMode();
    }

    @Override
    public void setPlayerTime(long l, boolean b) {
        player.setPlayerTime(l, b);
    }

    @Override
    public long getPlayerTime() {
        return player.getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return player.getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return player.isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        player.resetPlayerTime();
    }

    @Override
    public void setPlayerWeather(@NotNull WeatherType weatherType) {
        player.setPlayerWeather(weatherType);
    }

    @Override
    public @Nullable WeatherType getPlayerWeather() {
        return player.getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather() {
        player.resetPlayerWeather();
    }

    @Override
    public int getExpCooldown() {
        return player.getExpCooldown();
    }

    @Override
    public void setExpCooldown(int i) {
        player.setExpCooldown(i);
    }

    @Override
    public void giveExp(int i, boolean b) {
        player.giveExp(i, b);
    }

    @Override
    public int applyMending(int i) {
        return player.applyMending(i);
    }

    @Override
    public void giveExpLevels(int i) {
        player.giveExpLevels(i);
    }

    @Override
    public float getExp() {
        return player.getExp();
    }

    @Override
    public void setExp(float v) {
        player.setExp(v);
    }

    @Override
    public int getLevel() {
        return player.getLevel();
    }

    @Override
    public void setLevel(int i) {
        player.setLevel(i);
    }

    @Override
    public int getTotalExperience() {
        return player.getTotalExperience();
    }

    @Override
    public void setTotalExperience(int i) {
        player.setTotalExperience(i);
    }

    @Override
    public void sendExperienceChange(float v) {
        player.sendExperienceChange(v);
    }

    @Override
    public void sendExperienceChange(float v, int i) {
        player.sendExperienceChange(v, i);
    }

    @Override
    public boolean getAllowFlight() {
        return player.getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean b) {
        player.setAllowFlight(b);
    }

    @Override
    public void setFlyingFallDamage(@NotNull TriState triState) {
        player.setFlyingFallDamage(triState);
    }

    @Override
    public @NotNull TriState hasFlyingFallDamage() {
        return player.hasFlyingFallDamage();
    }

    @Override
    public void hidePlayer(@NotNull Player player) {
        player.hidePlayer(player);
    }

    @Override
    public void hidePlayer(@NotNull Plugin plugin, @NotNull Player player) {
        player.hidePlayer(plugin, player);
    }

    @Override
    public void showPlayer(@NotNull Player player) {
        player.showPlayer(player);
    }

    @Override
    public void showPlayer(@NotNull Plugin plugin, @NotNull Player player) {
        player.showPlayer(plugin, player);
    }

    @Override
    public boolean canSee(@NotNull Player player) {
        return player.canSee(player);
    }

    @Override
    public void hideEntity(@NotNull Plugin plugin, @NotNull Entity entity) {
        player.hideEntity(plugin, entity);
    }

    @Override
    public void showEntity(@NotNull Plugin plugin, @NotNull Entity entity) {
        player.showEntity(plugin, entity);
    }

    @Override
    public boolean canSee(@NotNull Entity entity) {
        return player.canSee(entity);
    }

    @Override
    public boolean isListed(@NotNull Player player) {
        return player.isListed(player);
    }

    @Override
    public boolean unlistPlayer(@NotNull Player player) {
        return player.unlistPlayer(player);
    }

    @Override
    public boolean listPlayer(@NotNull Player player) {
        return player.listPlayer(player);
    }

    @Override
    public boolean isFlying() {
        return player.isFlying();
    }

    @Override
    public void setFlying(boolean b) {
        player.setFlying(b);
    }

    @Override
    public void setFlySpeed(float v) throws IllegalArgumentException {
        player.setFlySpeed(v);
    }

    @Override
    public void setWalkSpeed(float v) throws IllegalArgumentException {
        player.setWalkSpeed(v);
    }

    @Override
    public float getFlySpeed() {
        return player.getFlySpeed();
    }

    @Override
    public float getWalkSpeed() {
        return player.getWalkSpeed();
    }

    @Override
    public void setTexturePack(@NotNull String s) {
        player.setTexturePack(s);
    }

    @Override
    public void setResourcePack(@NotNull String s) {
        player.setResourcePack(s);
    }

    @Override
    public void setResourcePack(@NotNull String s, @Nullable byte[] bytes) {
        player.setResourcePack(s, bytes);
    }

    @Override
    public void setResourcePack(@NotNull String s, @Nullable byte[] bytes, @Nullable String s1) {
        player.setResourcePack(s, bytes, s1);
    }

    @Override
    public void setResourcePack(@NotNull String s, @Nullable byte[] bytes, boolean b) {
        player.setResourcePack(s, bytes, b);
    }

    @Override
    public void setResourcePack(@NotNull String s, @Nullable byte[] bytes, @Nullable String s1, boolean b) {
        player.setResourcePack(s, bytes, s1, b);
    }

    @Override
    public void setResourcePack(@NotNull String s, byte @Nullable [] bytes, @Nullable Component component, boolean b) {
        player.setResourcePack(s, bytes, component, b);
    }

    @Override
    public @NotNull Scoreboard getScoreboard() {
        return player.getScoreboard();
    }

    @Override
    public void setScoreboard(@NotNull Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        player.setScoreboard(scoreboard);
    }

    @Override
    public @Nullable WorldBorder getWorldBorder() {
        return player.getWorldBorder();
    }

    @Override
    public void setWorldBorder(@Nullable WorldBorder worldBorder) {
        player.setWorldBorder(worldBorder);
    }

    @Override
    public void sendHealthUpdate(double v, int i, float v1) {
        player.sendHealthUpdate(v, i, v1);
    }

    @Override
    public void sendHealthUpdate() {
        player.sendHealthUpdate();
    }

    @Override
    public boolean isHealthScaled() {
        return player.isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean b) {
        player.setHealthScaled(b);
    }

    @Override
    public void setHealthScale(double v) throws IllegalArgumentException {
        player.setHealthScale(v);
    }

    @Override
    public double getHealthScale() {
        return player.getHealthScale();
    }

    @Override
    public @Nullable Entity getSpectatorTarget() {
        return player.getSpectatorTarget();
    }

    @Override
    public void setSpectatorTarget(@Nullable Entity entity) {
        player.setSpectatorTarget(entity);
    }

    @Override
    public void sendTitle(@Nullable String s, @Nullable String s1) {
        player.sendTitle(s, s1);
    }

    @Override
    public void sendTitle(@Nullable String s, @Nullable String s1, int i, int i1, int i2) {
        player.sendTitle(s, s1, i, i1, i2);
    }

    @Override
    public void resetTitle() {
        player.resetTitle();
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i) {
        player.spawnParticle(particle, location, i);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i) {
        player.spawnParticle(particle, v, v1, v2, i);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, @Nullable T t) {
        player.spawnParticle(particle, location, i, t);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, @Nullable T t) {
        player.spawnParticle(particle, v, v1, v2, i, t);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2) {
        player.spawnParticle(particle, location, i, v, v1, v2);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5) {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, @Nullable T t) {
        player.spawnParticle(particle, location, i, v, v1, v2, t);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, @Nullable T t) {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, t);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, double v3) {
        player.spawnParticle(particle, location, i, v, v1, v2, v3);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6) {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, double v3, @Nullable T t) {
        player.spawnParticle(particle, location, i, v, v1, v2, v3, t);
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, @Nullable T t) {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6, t);
    }

    @Override
    public @NotNull AdvancementProgress getAdvancementProgress(@NotNull Advancement advancement) {
        return player.getAdvancementProgress(advancement);
    }

    @Override
    public int getClientViewDistance() {
        return player.getClientViewDistance();
    }

    @Override
    public @NotNull Locale locale() {
        return player.locale();
    }

    @Override
    public int getPing() {
        return player.getPing();
    }

    @Override
    public @NotNull String getLocale() {
        return player.getLocale();
    }

    @Override
    public boolean getAffectsSpawning() {
        return player.getAffectsSpawning();
    }

    @Override
    public void setAffectsSpawning(boolean b) {
        player.setAffectsSpawning(b);
    }

    @Override
    public int getViewDistance() {
        return player.getViewDistance();
    }

    @Override
    public void setViewDistance(int i) {
        player.setViewDistance(i);
    }

    @Override
    public int getSimulationDistance() {
        return player.getSimulationDistance();
    }

    @Override
    public void setSimulationDistance(int i) {
        player.setSimulationDistance(i);
    }

    @Override
    public int getNoTickViewDistance() {
        return player.getNoTickViewDistance();
    }

    @Override
    public void setNoTickViewDistance(int i) {
        player.setNoTickViewDistance(i);
    }

    @Override
    public int getSendViewDistance() {
        return player.getSendViewDistance();
    }

    @Override
    public void setSendViewDistance(int i) {
        player.setSendViewDistance(i);
    }

    @Override
    public void updateCommands() {
        player.updateCommands();
    }

    @Override
    public void openBook(@NotNull ItemStack itemStack) {
        player.openBook(itemStack);
    }

    @Override
    public void openSign(@NotNull Sign sign) {
        player.openSign(sign);
    }

    @Override
    public void openSign(@NotNull Sign sign, @NotNull Side side) {
        player.openSign(sign, side);
    }

    @Override
    public boolean dropItem(boolean b) {
        return player.dropItem(b);
    }

    @Override
    public float getExhaustion() {
        return player.getExhaustion();
    }

    @Override
    public void setExhaustion(float v) {
        player.setExhaustion(v);
    }

    @Override
    public float getSaturation() {
        return player.getSaturation();
    }

    @Override
    public void setSaturation(float v) {
        player.setSaturation(v);
    }

    @Override
    public int getFoodLevel() {
        return player.getFoodLevel();
    }

    @Override
    public void setFoodLevel(int i) {
        player.setFoodLevel(i);
    }

    @Override
    public int getSaturatedRegenRate() {
        return player.getSaturatedRegenRate();
    }

    @Override
    public void setSaturatedRegenRate(int i) {
        player.setSaturatedRegenRate(i);
    }

    @Override
    public int getUnsaturatedRegenRate() {
        return player.getUnsaturatedRegenRate();
    }

    @Override
    public void setUnsaturatedRegenRate(int i) {
        player.setUnsaturatedRegenRate(i);
    }

    @Override
    public int getStarvationRate() {
        return player.getStarvationRate();
    }

    @Override
    public void setStarvationRate(int i) {
        player.setStarvationRate(i);
    }

    @Override
    public @Nullable Location getLastDeathLocation() {
        return player.getLastDeathLocation();
    }

    @Override
    public void setLastDeathLocation(@Nullable Location location) {
        player.setLastDeathLocation(location);
    }

    @Override
    public @Nullable Firework fireworkBoost(@NotNull ItemStack itemStack) {
        return player.fireworkBoost(itemStack);
    }

    @Override
    public void showDemoScreen() {
        player.showDemoScreen();
    }

    @Override
    public boolean isAllowingServerListings() {
        return player.isAllowingServerListings();
    }

    @Override
    public void setResourcePack(@NotNull String s, @NotNull String s1) {
        player.setResourcePack(s, s1);
    }

    @Override
    public void setResourcePack(@NotNull String s, @NotNull String s1, boolean b) {
        player.setResourcePack(s, s1, b);
    }

    @Override
    public void setResourcePack(@NotNull String s, @NotNull String s1, boolean b, @Nullable Component component) {
        player.setResourcePack(s, s1, b, component);
    }

    @Nullable
    @Override
    public PlayerResourcePackStatusEvent.Status getResourcePackStatus() {
        return player.getResourcePackStatus();
    }

    @Override
    public @Nullable String getResourcePackHash() {
        return player.getResourcePackHash();
    }

    @Override
    public boolean hasResourcePack() {
        return player.hasResourcePack();
    }

    @Override
    public @NotNull PlayerProfile getPlayerProfile() {
        return player.getPlayerProfile();
    }

    @Override
    public boolean isBanned() {
        return player.isBanned();
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Date date, @Nullable String s1) {
        return player.ban(s, date, s1);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Instant instant, @Nullable String s1) {
        return player.ban(s, instant, s1);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Duration duration, @Nullable String s1) {
        return player.ban(s, duration, s1);
    }

    @Override
    public boolean isWhitelisted() {
        return player.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean b) {
        player.setWhitelisted(b);
    }

    @Override
    public @Nullable Player getPlayer() {
        return player.getPlayer();
    }

    @Override
    public long getFirstPlayed() {
        return player.getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return player.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return player.hasPlayedBefore();
    }

    @Override
    public void setPlayerProfile(@NotNull PlayerProfile playerProfile) {
        player.setPlayerProfile(playerProfile);
    }

    @Override
    public float getCooldownPeriod() {
        return player.getCooldownPeriod();
    }

    @Override
    public float getCooledAttackStrength(float v) {
        return player.getCooledAttackStrength(v);
    }

    @Override
    public void resetCooldown() {
        player.resetCooldown();
    }

    @Override
    public <T> @NotNull T getClientOption(@NotNull ClientOption<T> clientOption) {
        return player.getClientOption(clientOption);
    }

    @Override
    public void sendOpLevel(byte b) {
        player.sendOpLevel(b);
    }

    @Override
    public void addAdditionalChatCompletions(@NotNull Collection<String> collection) {
        player.addAdditionalChatCompletions(collection);
    }

    @Override
    public void removeAdditionalChatCompletions(@NotNull Collection<String> collection) {
        player.removeAdditionalChatCompletions(collection);
    }

    @Override
    public @Nullable String getClientBrandName() {
        return player.getClientBrandName();
    }

    @Override
    public void setRotation(float v, float v1) {
        player.setRotation(v, v1);
    }

    @Override
    public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause teleportCause, @NotNull TeleportFlag @NotNull ... teleportFlags) {
        return player.teleport(location, teleportCause, teleportFlags);
    }

    @Override
    public boolean teleport(@NotNull Location location) {
        return player.teleport(location);
    }

    @Override
    public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause teleportCause) {
        return player.teleport(location, teleportCause);
    }

    @Override
    public boolean teleport(@NotNull Entity entity) {
        return player.teleport(entity);
    }

    @Override
    public boolean teleport(@NotNull Entity entity, @NotNull PlayerTeleportEvent.TeleportCause teleportCause) {
        return player.teleport(entity, teleportCause);
    }

    @Override
    public @NotNull List<Entity> getNearbyEntities(double v, double v1, double v2) {
        return player.getNearbyEntities(v, v1, v2);
    }

    @Override
    public int getEntityId() {
        return player.getEntityId();
    }

    @Override
    public int getFireTicks() {
        return player.getFireTicks();
    }

    @Override
    public int getMaxFireTicks() {
        return player.getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int i) {
        player.setFireTicks(i);
    }

    @Override
    public void setVisualFire(boolean b) {
        player.setVisualFire(b);
    }

    @Override
    public boolean isVisualFire() {
        return player.isVisualFire();
    }

    @Override
    public int getFreezeTicks() {
        return player.getFreezeTicks();
    }

    @Override
    public int getMaxFreezeTicks() {
        return player.getMaxFreezeTicks();
    }

    @Override
    public void setFreezeTicks(int i) {
        player.setFreezeTicks(i);
    }

    @Override
    public boolean isFrozen() {
        return player.isFrozen();
    }

    @Override
    public boolean isFreezeTickingLocked() {
        return player.isFreezeTickingLocked();
    }

    @Override
    public void lockFreezeTicks(boolean b) {
        player.lockFreezeTicks(b);
    }

    @Override
    public void remove() {
        player.remove();
    }

    @Override
    public boolean isDead() {
        return player.isDead();
    }

    @Override
    public boolean isValid() {
        return player.isValid();
    }

    @Override
    public void sendMessage(@NotNull String s) {
        player.sendMessage(s);
    }

    @Override
    public void sendMessage(@NotNull String... strings) {
        player.sendMessage(strings);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {
        player.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
        player.sendMessage(uuid, strings);
    }

    @Override
    public @NotNull Server getServer() {
        return player.getServer();
    }

    @Override
    public boolean isPersistent() {
        return player.isPersistent();
    }

    @Override
    public void setPersistent(boolean b) {
        player.setPersistent(b);
    }

    @Override
    public @Nullable Entity getPassenger() {
        return player.getPassenger();
    }

    @Override
    public boolean setPassenger(@NotNull Entity entity) {
        return player.setPassenger(entity);
    }

    @Override
    public @NotNull List<Entity> getPassengers() {
        return player.getPassengers();
    }

    @Override
    public boolean addPassenger(@NotNull Entity entity) {
        return player.addPassenger(entity);
    }

    @Override
    public boolean removePassenger(@NotNull Entity entity) {
        return player.removePassenger(entity);
    }

    @Override
    public boolean isEmpty() {
        return player.isEmpty();
    }

    @Override
    public boolean eject() {
        return player.eject();
    }

    @Override
    public float getFallDistance() {
        return player.getFallDistance();
    }

    @Override
    public void setFallDistance(float v) {
        player.setFallDistance(v);
    }

    @Override
    public void setLastDamageCause(@Nullable EntityDamageEvent entityDamageEvent) {
        player.setLastDamageCause(entityDamageEvent);
    }

    @Override
    public @Nullable EntityDamageEvent getLastDamageCause() {
        return player.getLastDamageCause();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public int getTicksLived() {
        return player.getTicksLived();
    }

    @Override
    public void setTicksLived(int i) {
        player.setTicksLived(i);
    }

    @Override
    public void playEffect(@NotNull EntityEffect entityEffect) {
        player.playEffect(entityEffect);
    }

    @Override
    public @NotNull EntityType getType() {
        return player.getType();
    }

    @Override
    public @NotNull Sound getSwimSound() {
        return player.getSwimSound();
    }

    @Override
    public @NotNull Sound getSwimSplashSound() {
        return player.getSwimSplashSound();
    }

    @Override
    public @NotNull Sound getSwimHighSpeedSplashSound() {
        return player.getSwimHighSpeedSplashSound();
    }

    @Override
    public boolean isInsideVehicle() {
        return player.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return player.leaveVehicle();
    }

    @Override
    public @Nullable Entity getVehicle() {
        return player.getVehicle();
    }

    @Override
    public void setCustomNameVisible(boolean b) {
        player.setCustomNameVisible(b);
    }

    @Override
    public boolean isCustomNameVisible() {
        return player.isCustomNameVisible();
    }

    @Override
    public void setVisibleByDefault(boolean b) {
        player.setVisibleByDefault(b);
    }

    @Override
    public boolean isVisibleByDefault() {
        return player.isVisibleByDefault();
    }

    @Override
    public @NotNull Set<Player> getTrackedBy() {
        return player.getTrackedBy();
    }

    @Override
    public void setGlowing(boolean b) {
        player.setGlowing(b);
    }

    @Override
    public boolean isGlowing() {
        return player.isGlowing();
    }

    @Override
    public void setInvulnerable(boolean b) {
        player.setInvulnerable(b);
    }

    @Override
    public boolean isInvulnerable() {
        return player.isInvulnerable();
    }

    @Override
    public boolean isSilent() {
        return player.isSilent();
    }

    @Override
    public void setSilent(boolean b) {
        player.setSilent(b);
    }

    @Override
    public boolean hasGravity() {
        return player.hasGravity();
    }

    @Override
    public void setGravity(boolean b) {
        player.setGravity(b);
    }

    @Override
    public int getPortalCooldown() {
        return player.getPortalCooldown();
    }

    @Override
    public void setPortalCooldown(int i) {
        player.setPortalCooldown(i);
    }

    @Override
    public @NotNull Set<String> getScoreboardTags() {
        return player.getScoreboardTags();
    }

    @Override
    public boolean addScoreboardTag(@NotNull String s) {
        return player.addScoreboardTag(s);
    }

    @Override
    public boolean removeScoreboardTag(@NotNull String s) {
        return player.removeScoreboardTag(s);
    }

    @Override
    public @NotNull PistonMoveReaction getPistonMoveReaction() {
        return player.getPistonMoveReaction();
    }

    @Override
    public @NotNull BlockFace getFacing() {
        return player.getFacing();
    }

    @Override
    public @NotNull Pose getPose() {
        return player.getPose();
    }

    @Override
    public void lookAt(double v, double v1, double v2, @NotNull LookAnchor lookAnchor) {
        player.lookAt(v, v1, v2, lookAnchor);
    }

    @Override
    public void lookAt(@NotNull Entity entity, @NotNull LookAnchor lookAnchor, @NotNull LookAnchor lookAnchor1) {
        player.lookAt(entity, lookAnchor, lookAnchor1);
    }

    @Override
    public void showElderGuardian(boolean b) {
        player.showElderGuardian(b);
    }

    @Override
    public int getWardenWarningCooldown() {
        return player.getWardenWarningCooldown();
    }

    @Override
    public void setWardenWarningCooldown(int i) {
        player.setWardenWarningCooldown(i);
    }

    @Override
    public int getWardenTimeSinceLastWarning() {
        return player.getWardenTimeSinceLastWarning();
    }

    @Override
    public void setWardenTimeSinceLastWarning(int i) {
        player.setWardenTimeSinceLastWarning(i);
    }

    @Override
    public int getWardenWarningLevel() {
        return player.getWardenWarningLevel();
    }

    @Override
    public void setWardenWarningLevel(int i) {
        player.setWardenWarningLevel(i);
    }

    @Override
    public void increaseWardenWarningLevel() {
        player.increaseWardenWarningLevel();
    }

    @Override
    public @NotNull Duration getIdleDuration() {
        return player.getIdleDuration();
    }

    @Override
    public void resetIdleDuration() {
        player.resetIdleDuration();
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return player.spigot();
    }

    @Override
    public @NotNull Component name() {
        return player.name();
    }

    @Override
    public @NotNull Component teamDisplayName() {
        return player.teamDisplayName();
    }

    @Override
    public @Nullable Location getOrigin() {
        return player.getOrigin();
    }

    @Override
    public boolean fromMobSpawner() {
        return player.fromMobSpawner();
    }

    @NotNull
    @Override
    public CreatureSpawnEvent.SpawnReason getEntitySpawnReason() {
        return player.getEntitySpawnReason();
    }

    @Override
    public boolean isUnderWater() {
        return player.isUnderWater();
    }

    @Override
    public boolean isInRain() {
        return player.isInRain();
    }

    @Override
    public boolean isInBubbleColumn() {
        return player.isInBubbleColumn();
    }

    @Override
    public boolean isInWaterOrRain() {
        return player.isInWaterOrRain();
    }

    @Override
    public boolean isInWaterOrBubbleColumn() {
        return player.isInWaterOrBubbleColumn();
    }

    @Override
    public boolean isInWaterOrRainOrBubbleColumn() {
        return player.isInWaterOrRainOrBubbleColumn();
    }

    @Override
    public boolean isInLava() {
        return player.isInLava();
    }

    @Override
    public boolean isTicking() {
        return player.isTicking();
    }

    @Override
    public @NotNull Set<Player> getTrackedPlayers() {
        return player.getTrackedPlayers();
    }

    @Override
    public boolean spawnAt(@NotNull Location location, @NotNull CreatureSpawnEvent.SpawnReason spawnReason) {
        return player.spawnAt(location, spawnReason);
    }

    @Override
    public boolean isInPowderedSnow() {
        return player.isInPowderedSnow();
    }

    @Override
    public double getX() {
        return player.getX();
    }

    @Override
    public double getY() {
        return player.getY();
    }

    @Override
    public double getZ() {
        return player.getZ();
    }

    @Override
    public float getPitch() {
        return player.getPitch();
    }

    @Override
    public float getYaw() {
        return player.getYaw();
    }

    @Override
    public boolean collidesAt(@NotNull Location location) {
        return player.collidesAt(location);
    }

    @Override
    public boolean wouldCollideUsing(@NotNull BoundingBox boundingBox) {
        return player.wouldCollideUsing(boundingBox);
    }

    @Override
    public @NotNull EntityScheduler getScheduler() {
        return player.getScheduler();
    }

    @Override
    public @NotNull String getScoreboardEntryName() {
        return player.getScoreboardEntryName();
    }

    @Override
    public void sendTitle(String text, int fadein, int showtime, int fadeout) {
        player.sendTitle(text, "", fadein, showtime, fadeout);
    }

    @Override
    public void sendSubTitle(String text, int fadein, int showtime, int fadeout) {
        player.sendTitle(text, "", fadein, showtime, fadeout);
    }

    @Override
    public boolean hasFullInventory() {
        return getInventory().firstEmpty() == -1;
    }

    @Override
    public boolean hasEmptyInventory() {
        for (ItemStack itemStack : getInventory()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) return false;
        }
        return true;
    }

    @Override
    public void giveItem(ItemStack item) {
        if (hasFullInventory()) {
            getWorld().dropItemNaturally(getLocation(), item);
        } else {
            getInventory().addItem(item);
        }
    }

    @Override
    public long getLastTeleport() {
        return this.lastTeleport;
    }

    @Override
    public void setLastTeleport(long time) {
        this.lastTeleport = time;
    }

    @Override
    public long getSessionJoin() {
        return this.sessionJoin;
    }

    @Override
    public Locale getLocalization() {
        return player.locale();
    }

    @Override
    public void setLocalization(Locale locale) {
        throw new UnsupportedOperationException("Cannot set locale of player.");
    }

    @Override
    public void sendLocalizedMessage(AbstractLocalizedMessage<DrapuriaPlayer, ?, MessageShowType, ?> localizedMessage) {
        localizedMessage.send(this);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return player.serialize();
    }

    @Override
    public @NotNull TriState getFrictionState() {
        return player.getFrictionState();
    }

    @Override
    public void setFrictionState(@NotNull TriState triState) {
        player.setFrictionState(triState);
    }

    @Override
    public @Nullable AttributeInstance getAttribute(@NotNull Attribute attribute) {
        return player.getAttribute(attribute);
    }

    @Override
    public void registerAttribute(@NotNull Attribute attribute) {
        player.registerAttribute(attribute);
    }

    @Override
    public void damage(double v) {
        player.damage(v);
    }

    @Override
    public void damage(double v, @Nullable Entity entity) {
        player.damage(v, entity);
    }

    @Override
    public double getHealth() {
        return player.getHealth();
    }

    @Override
    public void setHealth(double v) {
        player.setHealth(v);
    }

    @Override
    public double getAbsorptionAmount() {
        return player.getAbsorptionAmount();
    }

    @Override
    public void setAbsorptionAmount(double v) {
        player.setAbsorptionAmount(v);
    }

    @Override
    public double getMaxHealth() {
        return player.getMaxHealth();
    }

    @Override
    public void setMaxHealth(double v) {
        player.setMaxHealth(v);
    }

    @Override
    public void resetMaxHealth() {
        player.resetMaxHealth();
    }

    @Override
    public @Nullable Component customName() {
        return player.customName();
    }

    @Override
    public void customName(@Nullable Component component) {
        player.customName(component);
    }

    @Override
    public @Nullable String getCustomName() {
        return player.getCustomName();
    }

    @Override
    public void setCustomName(@Nullable String s) {
        player.setCustomName(s);
    }

    @Override
    public void setMetadata(@NotNull String s, @NotNull MetadataValue metadataValue) {
        player.setMetadata(s, metadataValue);
    }

    @Override
    public @NotNull List<MetadataValue> getMetadata(@NotNull String s) {
        return player.getMetadata(s);
    }

    @Override
    public boolean hasMetadata(@NotNull String s) {
        return player.hasMetadata(s);
    }

    @Override
    public void removeMetadata(@NotNull String s, @NotNull Plugin plugin) {
        player.removeMetadata(s, plugin);
    }

    @Override
    public boolean isPermissionSet(@NotNull String s) {
        return player.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return player.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(@NotNull String s) {
        return player.hasPermission(s);
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return player.hasPermission(permission);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return player.addAttachment(plugin, s, b);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return player.addAttachment(plugin);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return player.addAttachment(plugin, s, b, i);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return player.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
        player.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        player.recalculatePermissions();
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return player.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return player.isOp();
    }

    @Override
    public void setOp(boolean b) {
        player.setOp(b);
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return player.getPersistentDataContainer();
    }

    @Override
    public void sendPluginMessage(@NotNull Plugin plugin, @NotNull String s, @NotNull byte[] bytes) {
        player.sendPluginMessage(plugin, s, bytes);
    }

    @Override
    public @NotNull Set<String> getListeningPluginChannels() {
        return player.getListeningPluginChannels();
    }

    @Override
    public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> aClass) {
        return player.launchProjectile(aClass);
    }

    @Override
    public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> aClass, @Nullable Vector vector) {
        return player.launchProjectile(aClass, vector);
    }

    @Override
    public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> aClass, @Nullable Vector vector, @Nullable Consumer<? super T> consumer) {
        return player.launchProjectile(aClass, vector, consumer);
    }

    public Player player() {
        return player;
    }

    public long sessionJoin() {
        return sessionJoin;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DrapuriaPlayer1_20) obj;
        return Objects.equals(this.player, that.player) &&
                this.sessionJoin == that.sessionJoin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, sessionJoin);
    }

    @Override
    public String toString() {
        return "DrapuriaPlayer1_20[" +
                "player=" + player + ", " +
                "sessionJoin=" + sessionJoin + ']';
    }

}
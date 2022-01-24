/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.player;

import com.google.common.base.Preconditions;
import de.vantrex.hardcorespigot.modules.Module;
import de.vantrex.hardcorespigot.profiles.potion.PotionProfile;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Achievement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;

import java.net.InetSocketAddress;
import java.util.*;

public class DrapuriaPlayer1_8 implements DrapuriaPlayer {

    private final Player player;
    private final UUID uniqueId;

    public DrapuriaPlayer1_8(Player player) {
        this.player = player;
        this.uniqueId = player.getUniqueId();
    }

    @Override
    public void sendActionBar(String text) {
        ((CraftPlayer) player).getHandle().playerConnection
                .sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text.trim() + "\"}"), (byte) 2));
    }

    @Override
    public void sendTitle(String text, int fadein, int showtime, int fadeout) {

    }

    @Override
    public void sendSubTitle(String text, int fadein, int showtime, int fadeout) {

    }

    @Override
    public boolean hasFullInventory() {
        return getInventory().firstEmpty() == -1;
    }

    @Override
    public boolean hasEmptyInventory() {
        for (ItemStack itemStack : getInventory()) {
            if (itemStack != null && itemStack.getType() != Material.AIR)
                return false;
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

    public void teleportAsync(Location location) {
        teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void teleportAsync(Location location, PlayerTeleportEvent.TeleportCause cause) {
        Preconditions.checkArgument(location != null, "location");
        final Location locationClone = location.clone(); // clone so we don't need to worry about mutations after this call.
        ((CraftWorld) locationClone.getWorld()).getChunkAtAsync(location, chunk -> {
            MinecraftServer.getServer().postToMainThread(() -> teleport(location, cause));
        });
    }

    @Override
    public String getDisplayName() {
        return player.getDisplayName();
    }

    @Override
    public void setDisplayName(String s) {
        player.setDisplayName(s);
    }

    @Override
    public String getPlayerListName() {
        return player.getPlayerListName();
    }

    @Override
    public void setPlayerListName(String s) {
        player.setPlayerListName(s);
    }

    @Override
    public void setCompassTarget(Location location) {
        player.setCompassTarget(location);
    }

    @Override
    public Location getCompassTarget() {
        return player.getCompassTarget();
    }

    @Override
    public InetSocketAddress getAddress() {
        return player.getAddress();
    }

    @Override
    public boolean isConversing() {
        return player.isConversing();
    }

    @Override
    public void acceptConversationInput(String s) {
        player.acceptConversationInput(s);
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return player.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        player.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {
        player.abandonConversation(conversation, conversationAbandonedEvent);
    }

    @Override
    public void sendRawMessage(String s) {
        player.sendRawMessage(s);
    }

    @Override
    public void kickPlayer(String s) {
        player.kickPlayer(s);
    }

    @Override
    public void chat(String s) {
        player.chat(s);
    }

    @Override
    public boolean performCommand(String s) {
        return player.performCommand(s);
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
    public Module getKnockbackModule() {
        return player.getKnockbackModule();
    }

    @Override
    public void resetKnockbackModule() {
        player.resetKnockbackModule();
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

    /**
     * @param location
     * @param instrument
     * @param note
     * @deprecated
     */
    @Override
    public void playNote(Location location, byte instrument, byte note) {
        player.playNote(location, instrument, note);
    }

    @Override
    public void playNote(Location location, Instrument instrument, Note note) {
        player.playNote(location, instrument, note);
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        player.playSound(location, sound, volume, pitch);
    }

    @Override
    public void playSound(Location location, String s, float volume, float pitch) {
        player.playSound(location, s, volume, pitch);
    }

    @Override
    public void playEffect(Location location, Effect effect, int data) {
        player.playEffect(location, effect, data);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data) {
        player.playEffect(location, effect, data);
    }

    /**
     * @param location
     * @param material
     * @param b
     * @deprecated
     */
    @Override
    public void sendBlockChange(Location location, Material material, byte b) {
        player.sendBlockChange(location, material, b);
    }


    @Override
    public boolean sendChunkChange(Location location, int sx, int sy, int sz, byte[] data) {
        return player.sendChunkChange(location, sx, sy, sz, data);
    }

    /**
     * @param location
     * @param i
     * @param b
     * @deprecated
     */
    @Override
    public void sendBlockChange(Location location, int i, byte b) {
        player.sendBlockChange(location, i, b);
    }

    @Override
    public void sendSignChange(Location location, String[] strings) throws IllegalArgumentException {
        player.sendSignChange(location, strings);
    }

    @Override
    public void sendMap(MapView mapView) {
        player.sendMap(mapView);
    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {
        player.sendMessage(baseComponent);
    }

    @Override
    public void sendMessage(BaseComponent... baseComponents) {
        player.sendMessage(baseComponents);
    }

    @Override
    public void setPlayerListHeaderFooter(BaseComponent[] header, BaseComponent[] footer) {
        player.setPlayerListHeaderFooter(header, footer);
    }

    @Override
    public void setPlayerListHeaderFooter(BaseComponent header, BaseComponent footer) {
        player.setPlayerListHeaderFooter(header, footer);
    }

    @Override
    public void setTitleTimes(int fadein, int show, int fadeout) {
        player.setTitleTimes(fadein, show, fadeout);
    }

    @Override
    public void setSubtitle(BaseComponent[] subtitle) {
        player.setSubtitle(subtitle);
    }

    @Override
    public void setSubtitle(BaseComponent subtitle) {
        player.setSubtitle(subtitle);
    }

    @Override
    public void showTitle(BaseComponent[] title) {
        player.showTitle(title);
    }

    @Override
    public void showTitle(BaseComponent baseComponent) {
        player.showTitle(baseComponent);
    }

    @Override
    public void showTitle(BaseComponent[] baseComponents, BaseComponent[] baseComponents1, int i, int i1, int i2) {
        player.showTitle(baseComponents, baseComponents1, i, i1, i2);
    }


    @Override
    public void showTitle(BaseComponent baseComponent, BaseComponent baseComponent1, int i, int i1, int i2) {
        player.showTitle(baseComponent, baseComponent1, i, i1, i2);
    }

    @Override
    public void sendTitle(Title title) {
        player.sendTitle(title);
    }

    @Override
    public void updateTitle(Title title) {
        player.updateTitle(title);
    }

    @Override
    public void hideTitle() {
        player.hideTitle();
    }

    @Override
    public void updateInventory() {
        player.updateInventory();
    }

    @Override
    public void awardAchievement(Achievement achievement) {
        player.awardAchievement(achievement);
    }

    @Override
    public void removeAchievement(Achievement achievement) {
        player.removeAchievement(achievement);
    }

    @Override
    public boolean hasAchievement(Achievement achievement) {
        return player.hasAchievement(achievement);
    }

    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
        player.incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        player.decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        player.incrementStatistic(statistic, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        player.decrementStatistic(statistic, i);
    }

    @Override
    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {
        player.setStatistic(statistic, i);
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return player.getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        player.incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        player.decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return player.getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        player.incrementStatistic(statistic, material, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        player.decrementStatistic(statistic, material, i);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
        player.setStatistic(statistic, material, i);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        player.incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        player.decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return player.getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {
        player.incrementStatistic(statistic, entityType, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {
        player.incrementStatistic(statistic, entityType, i);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int i) {
        player.setStatistic(statistic, entityType, i);
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
    public void setPlayerWeather(WeatherType weatherType) {
        player.setPlayerWeather(weatherType);
    }

    @Override
    public WeatherType getPlayerWeather() {
        return player.getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather() {
        player.resetPlayerWeather();
    }

    @Override
    public void giveExp(int exp) {
        player.giveExp(exp);
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
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public boolean isBanned() {
        return player.isBanned();
    }

    @Override
    public void setBanned(boolean b) {
        player.setBanned(b);
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
    public Player getPlayer() {
        return player;
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
    public Location getBedSpawnLocation() {
        return player.getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location location) {
        player.setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean b) {
        player.setBedSpawnLocation(location, b);
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
    public void hidePlayer(Player player) {
        player.hidePlayer(player);
    }

    @Override
    public void showPlayer(Player player) {
        player.showPlayer(player);
    }

    @Override
    public PotionProfile getPotionProfile() {
        return player.getPotionProfile();
    }

    @Override
    public void setPotionProfile(PotionProfile potionProfile) {
        player.setPotionProfile(potionProfile);
    }

    @Override
    public boolean canSee(Player player) {
        return player.canSee(player);
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public Location getLocation(Location location) {
        return player.getLocation(location);
    }

    @Override
    public void setVelocity(Vector vector) {
        player.setVelocity(vector);
    }

    @Override
    public Vector getVelocity() {
        return player.getVelocity();
    }

    /**
     * @deprecated
     */
    @Override
    public boolean isOnGround() {
        return player.isOnGround();
    }

    @Override
    public World getWorld() {
        return player.getWorld();
    }

    @Override
    public boolean teleport(Location location) {
        return player.teleport(location);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return player.teleport(location, teleportCause);
    }

    @Override
    public boolean teleport(Entity entity) {
        return player.teleport(entity);
    }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
        return player.teleport(entity, teleportCause);
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return player.getNearbyEntities(x, y, z);
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
    public void sendMessage(String s) {
        player.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        player.sendMessage(strings);
    }

    @Override
    public Server getServer() {
        return player.getServer();
    }

    @Override
    public Entity getPassenger() {
        return player.getPassenger();
    }

    @Override
    public boolean setPassenger(Entity entity) {
        return player.setPassenger(entity);
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
    public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {
        player.setLastDamageCause(entityDamageEvent);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return player.getLastDamageCause();
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
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
    public void playEffect(EntityEffect entityEffect) {
        player.playEffect(entityEffect);
    }

    @Override
    public EntityType getType() {
        return player.getType();
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
    public Entity getVehicle() {
        return player.getVehicle();
    }

    @Override
    public void setCustomName(String s) {
        player.setCustomName(s);
    }

    @Override
    public String getCustomName() {
        return player.getCustomName();
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

    /**
     * @param s
     * @deprecated
     */
    @Override
    public void setTexturePack(String s) {
        player.setTexturePack(s);
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public void setResourcePack(String s) {
        player.setResourcePack(s);
    }

    @Override
    public Scoreboard getScoreboard() {
        return player.getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        player.setScoreboard(scoreboard);
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
    public Entity getSpectatorTarget() {
        return player.getSpectatorTarget();
    }

    @Override
    public void setSpectatorTarget(Entity entity) {
        player.setSpectatorTarget(entity);
    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public void sendTitle(String s, String s1) {
        player.sendTitle(s, s1);
    }

    @Override
    public void resetTitle() {
        player.resetTitle();
    }

    @Override
    public void setResourcePack(String s, String s1) {
        player.setResourcePack(s, s1);
    }

    @Override
    public PlayerResourcePackStatusEvent.Status getResourcePackStatus() {
        return player.getResourcePackStatus();
    }

    @Override
    public String getResourcePackHash() {
        return player.getResourcePackHash();
    }

    @Override
    public boolean hasResourcePack() {
        return player.hasResourcePack();
    }

    @Override
    public Spigot spigot() {
        return player.spigot();
    }

    @Override
    public Map<String, Object> serialize() {
        return player.serialize();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public PlayerInventory getInventory() {
        return player.getInventory();
    }

    @Override
    public Inventory getEnderChest() {
        return player.getEnderChest();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property property, int i) {
        return player.setWindowProperty(property, i);
    }

    @Override
    public InventoryView getOpenInventory() {
        return player.getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory inventory) {
        return player.openInventory(inventory);
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean b) {
        return player.openWorkbench(location, b);
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean b) {
        return player.openEnchanting(location, b);
    }

    @Override
    public void openInventory(InventoryView inventoryView) {
        player.openInventory(inventoryView);
    }

    @Override
    public void closeInventory() {
        player.closeInventory();
    }

    @Override
    public ItemStack getItemInHand() {
        return player.getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack itemStack) {
        player.setItemInHand(itemStack);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return player.getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack itemStack) {
        player.setItemOnCursor(itemStack);
    }

    @Override
    public boolean isSleeping() {
        return player.isSleeping();
    }

    @Override
    public int getSleepTicks() {
        return player.getSleepTicks();
    }

    @Override
    public GameMode getGameMode() {
        return player.getGameMode();
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        player.setGameMode(gameMode);
    }

    @Override
    public boolean isBlocking() {
        return player.isBlocking();
    }

    @Override
    public int getExpToLevel() {
        return player.getExpToLevel();
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
    public Location getEyeLocation() {
        return player.getEyeLocation();
    }

    /**
     * @param hashSet
     * @param i
     * @deprecated
     */
    @Override
    public List<Block> getLineOfSight(HashSet<Byte> hashSet, int i) {
        return player.getLineOfSight(hashSet, i);
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return player.getLineOfSight(set, i);
    }

    /**
     * @param hashSet
     * @param i
     * @deprecated
     */
    @Override
    public Block getTargetBlock(HashSet<Byte> hashSet, int i) {
        return player.getTargetBlock(hashSet, i);
    }

    @Override
    public Block getTargetBlock(Set<Material> set, int i) {
        return player.getTargetBlock(set, i);
    }

    /**
     * @param hashSet
     * @param i
     * @deprecated
     */
    @Override
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hashSet, int i) {
        return player.getLastTwoTargetBlocks(hashSet, i);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        return player.getLastTwoTargetBlocks(set, i);
    }

    /**
     * @deprecated
     */
    @Override
    public Egg throwEgg() {
        return player.throwEgg();
    }

    /**
     * @deprecated
     */
    @Override
    public Snowball throwSnowball() {
        return player.throwSnowball();
    }

    /**
     * @deprecated
     */
    @Override
    public Arrow shootArrow() {
        return player.shootArrow();
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
    public Player getKiller() {
        return player.getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect) {
        return player.addPotionEffect(potionEffect);
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
        return player.addPotionEffect(potionEffect, b);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> collection) {
        return player.addPotionEffects(collection);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffectType) {
        return player.hasPotionEffect(potionEffectType);
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {
        player.removePotionEffect(potionEffectType);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return player.getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return player.hasLineOfSight(entity);
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
    public EntityEquipment getEquipment() {
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
    public Entity getLeashHolder() throws IllegalStateException {
        return player.getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity entity) {
        return player.setLeashHolder(entity);
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
    public void damage(double v) {
        player.damage(v);
    }

    @Override
    public void damage(double v, Entity entity) {
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
    public void setMetadata(String s, MetadataValue metadataValue) {
        player.setMetadata(s, metadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return player.getMetadata(s);
    }

    @Override
    public boolean hasMetadata(String s) {
        return player.hasMetadata(s);
    }

    @Override
    public void removeMetadata(String s, Plugin plugin) {
        player.removeMetadata(s, plugin);
    }

    @Override
    public boolean isPermissionSet(String s) {
        return player.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return player.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String s) {
        return player.hasPermission(s);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return player.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return player.addAttachment(plugin, s, b);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return player.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return player.addAttachment(plugin, s, b, i);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return player.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        player.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        player.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
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
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {
        player.sendPluginMessage(plugin, s, bytes);
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return player.getListeningPluginChannels();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return player.launchProjectile(aClass);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
        return player.launchProjectile(aClass, vector);
    }
}

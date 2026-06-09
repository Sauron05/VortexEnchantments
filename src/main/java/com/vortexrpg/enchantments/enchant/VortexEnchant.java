package com.vortexrpg.enchantments.enchant;

import com.vortexrpg.enchantments.VortexEnchantments;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all VortexEnchantments custom enchantments.
 * Subclasses override only the event methods they need.
 */
public abstract class VortexEnchant {

    protected final VortexEnchantments plugin;
    protected final String id;
    protected final String displayName;
    protected final EnchantRarity rarity;
    protected final int maxLevel;
    protected final List<ItemTarget> targets;

    private List<String> conflicts = new ArrayList<>();
    private boolean enabled = true;
    protected ConfigurationSection config;

    protected VortexEnchant(String id, String displayName, EnchantRarity rarity, int maxLevel, List<ItemTarget> targets) {
        this.plugin = VortexEnchantments.getInstance();
        this.id = id;
        this.displayName = displayName;
        this.rarity = rarity;
        this.maxLevel = maxLevel;
        this.targets = new ArrayList<>(targets);
    }

    /** Convenience constructor for enchants that specify target as a string. */
    protected VortexEnchant(String id, String displayName, String targetType) {
        this(id, displayName, EnchantRarity.COMMON, 1, mapTarget(targetType));
    }

    private static List<ItemTarget> mapTarget(String s) {
        return switch (s == null ? "" : s.toLowerCase()) {
            case "sword" -> List.of(ItemTarget.SWORD);
            case "axe" -> List.of(ItemTarget.AXE);
            case "bow" -> List.of(ItemTarget.BOW);
            case "crossbow" -> List.of(ItemTarget.CROSSBOW);
            case "trident" -> List.of(ItemTarget.TRIDENT);
            case "pickaxe" -> List.of(ItemTarget.PICKAXE);
            case "shovel" -> List.of(ItemTarget.SHOVEL);
            case "hoe" -> List.of(ItemTarget.HOE);
            case "helmet" -> List.of(ItemTarget.HELMET);
            case "chestplate" -> List.of(ItemTarget.CHESTPLATE);
            case "leggings" -> List.of(ItemTarget.LEGGINGS);
            case "boots" -> List.of(ItemTarget.BOOTS);
            case "shield" -> List.of(ItemTarget.SHIELD);
            case "elytra" -> List.of(ItemTarget.ELYTRA);
            case "fishingrod", "fishing_rod" -> List.of(ItemTarget.FISHING_ROD);
            default -> new ArrayList<>();
        };
    }

    // ─── Event hooks (override as needed) ───────────────────────────────────

    /** Called when the holder attacks a LivingEntity with the enchanted item. */
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        onAttack(event, attacker, level);
    }

    /** 3-arg attack variant (no victim param) for shield/buckler enchants. */
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, int level) {}

    /** Called when the holder kills a LivingEntity. */
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        onKill(event, killer, level);
    }

    /** 3-arg kill variant (no killed-entity param) for trident enchants. */
    public void onKill(EntityDeathEvent event, Player killer, int level) {}

    /** Kill variant using EntityDamageByEntityEvent (for chestplate/helmet enchants). */
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {}

    /** Called when the holder breaks a block. */
    public void onBlockBreak(BlockBreakEvent event, Player player, Block block, int level) {
        onBlockBreak(event, player, level);
    }

    /** 3-arg block-break variant (no Block param) for pickaxe/shovel/hoe enchants. */
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {}

    /** Called on left/right click with enchanted item. */
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack item, int level) {
        onInteract(event, player, level); // delegate to 3-arg override if present
    }

    /** Called on left/right click with enchanted item (convenience 3-arg override). */
    public void onInteract(PlayerInteractEvent event, Player player, int level) {}

    /** Called when the holder is damaged by an entity. */
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {}

    /** Called when a player shoots with this item (bow/crossbow/trident). */
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {}

    /** Called when a projectile from this bow/crossbow hits an entity. */
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {}

    /** Called when a projectile from this bow/crossbow hits a block. */
    public void onArrowHitBlock(ProjectileHitEvent event, Player shooter, int level) {}

    /** Called when a fishing rod catches/hooks something. */
    public void onFish(PlayerFishEvent event, Player player, int level) {}

    /** Called every player movement tick while item is equipped. */
    public void onMove(PlayerMoveEvent event, Player player, int level) {}

    /** Called when a player toggles sneak with item held/equipped. */
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {}

    /** ToggleSneak variant without event param (Player + isSneaking flag). */
    public void onToggleSneak(Player player, boolean isSneaking, int level) {}

    /** Called on player respawn if item is in their inventory. */
    public void onRespawn(PlayerRespawnEvent event, Player player, int level) {
        onRespawn(player, level);
    }

    /** 2-arg respawn variant (no event param) for leggings/chestplate enchants. */
    public void onRespawn(Player player, int level) {}

    /** Called every second from the passive repeating task. */
    public void tickPassive(Player player, int level) {}

    /** Called when the holder scoops up/uses a trident. */
    public void onTridentHit(ProjectileHitEvent event, Player holder, int level) {}

    /** Trident-hit variant with target entity (for damage-dealing trident enchants). */
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {}

    /** Called when the player harvests a crop block. */
    public void onHarvest(PlayerHarvestBlockEvent event, Player player, int level) {}

    /** Harvest variant using BlockBreakEvent (hoe breaks a crop block). */
    public void onHarvest(BlockBreakEvent event, Player player, int level) {}

    /** Called when a player takes any damage (not necessarily from entity). */
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {}

    /** Called when a player takes damage from another entity (convenience override). */
    public void onDamageTaken(org.bukkit.event.entity.EntityDamageByEntityEvent event, Player player, int level) {}

    // ─── Required abstracts ──────────────────────────────────────────────────

    public String getDescription() { return getDescription(1); }
    public abstract String getDescription(int level);

    // ─── Config helpers ──────────────────────────────────────────────────────

    protected double cfg(String key, double def) {
        return config != null ? config.getDouble(key, def) : def;
    }

    protected double cfgd(String key, double def) {
        return cfg(key, def);
    }

    protected int cfgi(String key, int def) {
        return config != null ? config.getInt(key, def) : def;
    }

    protected boolean cfgb(String key, boolean def) {
        return config != null ? config.getBoolean(key, def) : def;
    }

    protected String cfgs(String key, String def) {
        return config != null ? config.getString(key, def) : def;
    }

    protected List<String> cfglist(String key) {
        return config != null ? config.getStringList(key) : new ArrayList<>();
    }

    // ─── Cooldown helpers ────────────────────────────────────────────────────

    protected boolean isOnCooldown(Player player) {
        return plugin.getCooldownManager().isOnCooldown(player, id);
    }

    protected long getRemainingCooldown(Player player) {
        return plugin.getCooldownManager().getRemainingMillis(player, id);
    }

    protected void setCooldown(Player player, long millis) {
        plugin.getCooldownManager().setCooldown(player, id, millis);
    }

    protected void setCooldownSeconds(Player player, double seconds) {
        setCooldown(player, (long) (seconds * 1000));
    }

    protected void setCooldownFromConfig(Player player, String key, double defaultSeconds) {
        setCooldownSeconds(player, cfg(key, defaultSeconds));
    }

    // ─── Getters / setters ───────────────────────────────────────────────────

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public EnchantRarity getRarity() { return rarity; }
    public String getTier() { return rarity.name(); }
    public int getMaxLevel() { return maxLevel; }
    public List<ItemTarget> getTargets() { return targets; }
    public List<String> getConflicts() { return conflicts; }
    public boolean isEnabled() { return enabled; }
    public ConfigurationSection getConfig() { return config; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setConflicts(List<String> conflicts) { this.conflicts = new ArrayList<>(conflicts); }
    public void setConfig(ConfigurationSection config) { this.config = config; }

    public NamespacedKey getPDCKey() {
        return new NamespacedKey(plugin, "enchant_" + id);
    }

    /** Returns the roman numeral display for a given level. */
    public String getLevelDisplay(int level) {
        if (maxLevel == 1) return "";
        return " " + toRoman(level);
    }

    /** Full formatted lore line, e.g. "§5Debt II §8[§5Epic§8]" */
    public String getLoreLine(int level) {
        return rarity.getColor() + displayName + getLevelDisplay(level);
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV";
            case 5 -> "V"; case 6 -> "VI"; case 7 -> "VII"; case 8 -> "VIII";
            case 9 -> "IX"; case 10 -> "X";
            default -> String.valueOf(n);
        };
    }

    @Override
    public String toString() {
        return "VortexEnchant{id=" + id + ", rarity=" + rarity + ", maxLevel=" + maxLevel + "}";
    }
}

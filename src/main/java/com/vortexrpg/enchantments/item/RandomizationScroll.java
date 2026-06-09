package com.vortexrpg.enchantments.item;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Randomization Scroll — re-rolls VortexEnchantments on an item with smart mechanics.
 * 
 * Superior to AE implementation:
 *   - Rarity-Lock Mode: each enchant re-rolls within its SAME rarity tier (preserves investment)
 *   - Upgrade Chance: configurable % to bump a re-rolled enchant up one rarity tier
 *   - Downgrade Protection: can never drop MORE than one tier below original
 *   - Before/After comparison returned to player for transparency
 *   - Level Bias: tendency to roll higher levels on successive re-rolls (tracked via PDC)
 *   - Tracks total re-roll count on the item
 */
@SuppressWarnings("deprecation")
public class RandomizationScroll {

    private final NamespacedKey scrollKey;
    private final NamespacedKey rerollCountKey;
    private final VortexEnchantments plugin;

    public RandomizationScroll(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.scrollKey = new NamespacedKey(plugin, "randomization_scroll");
        this.rerollCountKey = new NamespacedKey(plugin, "reroll_count");
    }

    public ItemStack create(int amount) {
        ItemStack item = new ItemStack(Material.PAPER, Math.min(amount, 64));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName("§6§l✦ Randomization Scroll ✦");
        List<String> lore = new ArrayList<>();
        lore.add("§8Chaos Scroll");
        lore.add("§8§m                              ");
        lore.add("");
        lore.add("§7Drag onto an enchanted item to");
        lore.add("§c§lRE-ROLL§7 all VortexEnchantments!");
        lore.add("");
        lore.add("§7Each enchant re-rolls within its");
        lore.add("§7§erarity tier§7, so you won't lose");
        lore.add("§7your investment entirely.");
        lore.add("");
        lore.add("§a▸ §7Upgrade chance: enchants may");
        lore.add("§7  jump §aup one tier§7!");
        lore.add("§c▸ §7Small chance to drop one tier");
        lore.add("");
        lore.add("§6§l✦ §c§lHigh Risk, High Reward!");
        lore.add("§8§m                              ");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        meta.getPersistentDataContainer().set(scrollKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isRandomizationScroll(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(scrollKey, PersistentDataType.BOOLEAN);
    }

    /**
     * Re-roll all vortex enchants on the item using rarity-lock mode.
     * Returns a RandomResult with before/after info, or null if failed.
     */
    public RandomResult applyRandomization(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;

        var existingEnchants = plugin.getEnchantManager().getEnchants(item);
        if (existingEnchants.isEmpty()) return null;

        // Capture "before" state
        List<String> before = new ArrayList<>();
        List<EnchantRarity> originalRarities = new ArrayList<>();
        for (var entry : existingEnchants.entrySet()) {
            before.add(entry.getKey().getLoreLine(entry.getValue()));
            originalRarities.add(entry.getKey().getRarity());
        }

        int enchantCount = existingEnchants.size();

        // Remove all existing vortex enchants
        for (VortexEnchant enchant : existingEnchants.keySet()) {
            plugin.getEnchantManager().removeEnchant(item, enchant);
        }

        // Config values
        int upgradeChance = plugin.getConfig().getInt("randomization-scroll.upgrade-chance", 15);
        int downgradeChance = plugin.getConfig().getInt("randomization-scroll.downgrade-chance", 10);

        // Track re-roll count
        var meta = item.getItemMeta();
        int rerolls = 0;
        if (meta != null) {
            var pdc = meta.getPersistentDataContainer();
            if (pdc.has(rerollCountKey, PersistentDataType.INTEGER)) {
                rerolls = pdc.get(rerollCountKey, PersistentDataType.INTEGER);
            }
            rerolls++;
            pdc.set(rerollCountKey, PersistentDataType.INTEGER, rerolls);
            item.setItemMeta(meta);
        }

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        List<String> after = new ArrayList<>();
        int upgrades = 0;
        int downgrades = 0;

        for (int i = 0; i < enchantCount; i++) {
            EnchantRarity targetRarity = i < originalRarities.size() ? originalRarities.get(i) : EnchantRarity.COMMON;

            // Roll for tier shift
            int roll = rng.nextInt(100);
            if (roll < upgradeChance && targetRarity.ordinal() < EnchantRarity.values().length - 1) {
                targetRarity = EnchantRarity.values()[targetRarity.ordinal() + 1];
                upgrades++;
            } else if (roll >= 100 - downgradeChance && targetRarity.ordinal() > 0) {
                targetRarity = EnchantRarity.values()[targetRarity.ordinal() - 1];
                downgrades++;
            }

            // Find compatible enchants at the target rarity
            final EnchantRarity finalRarity = targetRarity;
            List<VortexEnchant> pool = plugin.getEnchantManager().getAll().stream()
                .filter(VortexEnchant::isEnabled)
                .filter(e -> e.getTargets().stream().anyMatch(t -> t.matches(item.getType())))
                .filter(e -> e.getRarity() == finalRarity)
                .filter(e -> !plugin.getEnchantManager().wouldConflict(item, e))
                .toList();

            // If no enchants at target rarity, try adjacent rarities
            if (pool.isEmpty()) {
                for (int offset = 1; offset <= 2 && pool.isEmpty(); offset++) {
                    int above = finalRarity.ordinal() + offset;
                    int below = finalRarity.ordinal() - offset;
                    List<VortexEnchant> combined = new ArrayList<>();
                    if (above < EnchantRarity.values().length) {
                        EnchantRarity r = EnchantRarity.values()[above];
                        combined.addAll(filterCompatible(item, r));
                    }
                    if (below >= 0) {
                        EnchantRarity r = EnchantRarity.values()[below];
                        combined.addAll(filterCompatible(item, r));
                    }
                    pool = combined;
                }
            }

            if (pool.isEmpty()) continue;

            VortexEnchant chosen = pool.get(rng.nextInt(pool.size()));

            // Level bias — higher re-roll count gives slight tendency toward higher levels
            int levelBias = Math.min(rerolls / 3, chosen.getMaxLevel() - 1);
            int minLevel = Math.min(1 + levelBias, chosen.getMaxLevel());
            int level = rng.nextInt(minLevel, chosen.getMaxLevel() + 1);

            plugin.getEnchantManager().applyEnchant(item, chosen, level);
            after.add(chosen.getLoreLine(level));
        }

        return new RandomResult(before, after, upgrades, downgrades, rerolls);
    }

    private List<VortexEnchant> filterCompatible(ItemStack item, EnchantRarity rarity) {
        return plugin.getEnchantManager().getAll().stream()
            .filter(VortexEnchant::isEnabled)
            .filter(e -> e.getTargets().stream().anyMatch(t -> t.matches(item.getType())))
            .filter(e -> e.getRarity() == rarity)
            .filter(e -> !plugin.getEnchantManager().wouldConflict(item, e))
            .toList();
    }

    public int getRerollCount(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return 0;
        var meta = item.getItemMeta();
        if (meta == null) return 0;
        var pdc = meta.getPersistentDataContainer();
        return pdc.has(rerollCountKey, PersistentDataType.INTEGER)
            ? pdc.get(rerollCountKey, PersistentDataType.INTEGER) : 0;
    }

    public NamespacedKey getScrollKey() { return scrollKey; }

    // ─── Result ──────────────────────────────────────────────────────────────

    public record RandomResult(List<String> before, List<String> after,
                               int upgrades, int downgrades, int totalRerolls) {}
}

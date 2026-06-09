package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hooks into the vanilla enchanting table to occasionally add VortexEnchantments
 * alongside or instead of vanilla enchantments.
 */
public class EnchantTableListener implements Listener {

    private final VortexEnchantments plugin;

    public EnchantTableListener(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {
        if (!plugin.getConfigManager().isEnchantTableEnabled()) return;

        double chance = plugin.getConfig().getDouble("enchanting-table.vortex-chance", 25.0);
        if (ThreadLocalRandom.current().nextDouble(100) > chance) return;

        ItemStack item = event.getItem();

        // Determine eligible enchants for this item type
        List<VortexEnchant> eligible = plugin.getEnchantManager().getAll().stream()
            .filter(VortexEnchant::isEnabled)
            .filter(e -> e.getTargets().stream().anyMatch(t -> t.matches(item.getType())))
            .toList();

        if (eligible.isEmpty()) return;

        // Select rarity tier based on enchanting level
        int cost = event.getExpLevelCost();
        EnchantRarity tier = selectTier(cost);

        // Filter by rarity
        List<VortexEnchant> tiered = eligible.stream()
            .filter(e -> e.getRarity() == tier || (cost >= 30 && e.getRarity().ordinal() <= tier.ordinal()))
            .toList();

        if (tiered.isEmpty()) {
            // Fallback to any eligible
            tiered = eligible;
        }

        // Pick a random enchant
        VortexEnchant chosen = tiered.get(ThreadLocalRandom.current().nextInt(tiered.size()));

        // Determine level: higher enchanting cost = higher level chance
        int maxLevel = chosen.getMaxLevel();
        int level = Math.min(maxLevel, Math.max(1, (int) Math.ceil((double) cost / 30.0 * maxLevel)));

        // Check conflicts with existing vortex enchants
        if (plugin.getEnchantManager().wouldConflict(item, chosen)) return;

        // Apply the enchant via PDC
        plugin.getEnchantManager().applyEnchant(item, chosen, level);

        // Send discovery message
        event.getEnchanter().sendMessage("§5✦ §dNew enchantment discovered: "
            + chosen.getLoreLine(level) + " §5✦");
    }

    private EnchantRarity selectTier(int enchantCost) {
        // Higher enchanting table cost = higher chance for rare enchants
        if (enchantCost >= 30) {
            double roll = ThreadLocalRandom.current().nextDouble(100);
            if (roll < 2) return EnchantRarity.MYTHIC;
            if (roll < 7) return EnchantRarity.LEGENDARY;
            if (roll < 20) return EnchantRarity.EPIC;
            if (roll < 45) return EnchantRarity.RARE;
            if (roll < 75) return EnchantRarity.UNCOMMON;
            return EnchantRarity.COMMON;
        } else if (enchantCost >= 15) {
            double roll = ThreadLocalRandom.current().nextDouble(100);
            if (roll < 1) return EnchantRarity.LEGENDARY;
            if (roll < 8) return EnchantRarity.EPIC;
            if (roll < 25) return EnchantRarity.RARE;
            if (roll < 60) return EnchantRarity.UNCOMMON;
            return EnchantRarity.COMMON;
        } else {
            double roll = ThreadLocalRandom.current().nextDouble(100);
            if (roll < 3) return EnchantRarity.RARE;
            if (roll < 20) return EnchantRarity.UNCOMMON;
            return EnchantRarity.COMMON;
        }
    }
}

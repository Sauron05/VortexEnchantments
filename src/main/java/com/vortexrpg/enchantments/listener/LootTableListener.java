package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Injects VortexEnchantment books into naturally generated loot
 * (dungeon chests, temples, end cities, etc.).
 */
public class LootTableListener implements Listener {

    private final VortexEnchantments plugin;

    public LootTableListener(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        if (!plugin.getConfig().getBoolean("loot-tables.enabled", true)) return;

        double chance = plugin.getConfig().getDouble("loot-tables.injection-chance", 15.0);
        if (ThreadLocalRandom.current().nextDouble(100) > chance) return;

        // Select tier based on loot context
        String lootTableKey = event.getLootTable().getKey().getKey();
        EnchantRarity tier = selectTierForLootTable(lootTableKey);

        List<VortexEnchant> eligible = plugin.getEnchantManager().getAll().stream()
            .filter(VortexEnchant::isEnabled)
            .filter(e -> e.getRarity().ordinal() <= tier.ordinal())
            .toList();

        if (eligible.isEmpty()) return;

        // Weight by rarity (lower rarity = more likely)
        VortexEnchant chosen = selectWeighted(eligible);
        int level = Math.max(1, ThreadLocalRandom.current().nextInt(1, Math.min(3, chosen.getMaxLevel()) + 1));

        ItemStack book = plugin.getEnchantManager().createEnchantedBook(chosen, level);
        event.getLoot().add(book);
    }

    private EnchantRarity selectTierForLootTable(String key) {
        if (key.contains("end_city") || key.contains("ancient_city") || key.contains("bastion")) {
            return EnchantRarity.LEGENDARY;
        }
        if (key.contains("stronghold") || key.contains("woodland_mansion") || key.contains("nether")) {
            return EnchantRarity.EPIC;
        }
        if (key.contains("desert_pyramid") || key.contains("jungle_temple") || key.contains("ruined_portal")) {
            return EnchantRarity.RARE;
        }
        if (key.contains("mineshaft") || key.contains("underwater_ruin") || key.contains("shipwreck")) {
            return EnchantRarity.UNCOMMON;
        }
        return EnchantRarity.COMMON;
    }

    private VortexEnchant selectWeighted(List<VortexEnchant> enchants) {
        int totalWeight = enchants.stream()
            .mapToInt(e -> e.getRarity().getDefaultWeight())
            .sum();
        int roll = ThreadLocalRandom.current().nextInt(totalWeight);
        int cumulative = 0;
        for (VortexEnchant e : enchants) {
            cumulative += e.getRarity().getDefaultWeight();
            if (roll < cumulative) return e;
        }
        return enchants.get(enchants.size() - 1);
    }
}

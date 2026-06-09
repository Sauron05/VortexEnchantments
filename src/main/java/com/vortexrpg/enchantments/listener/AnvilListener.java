package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantManager;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class AnvilListener implements Listener {

    private final VortexEnchantments plugin;
    private final EnchantManager enchantManager;

    public AnvilListener(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.enchantManager = plugin.getEnchantManager();
    }

    @SuppressWarnings("removal")
    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!plugin.getConfigManager().isAnvilEnabled()) return;

        AnvilInventory anvil = event.getInventory();
        ItemStack base = anvil.getItem(0);
        ItemStack material = anvil.getItem(1);

        if (base == null || base.getType() == Material.AIR) return;
        if (material == null || material.getType() == Material.AIR) return;

        // Only intervene when the material contains vortex enchants
        Map<VortexEnchant, Integer> bookEnchants = enchantManager.getEnchants(material);
        if (bookEnchants.isEmpty()) return;

        // Combine the items (combineItems clones internally)
        ItemStack result = enchantManager.combineItems(base, material);

        // If nothing was transferred, leave vanilla behaviour untouched
        Map<VortexEnchant, Integer> baseEnchants = enchantManager.getEnchants(base);
        Map<VortexEnchant, Integer> resultEnchants = enchantManager.getEnchants(result);
        if (resultEnchants.equals(baseEnchants)) return;

        // Calculate XP cost: (rarity ordinal 1-6) × level per enchant applied
        int cost = 0;
        for (Map.Entry<VortexEnchant, Integer> entry : bookEnchants.entrySet()) {
            VortexEnchant enchant = entry.getKey();
            int level = entry.getValue();
            if (!resultEnchants.containsKey(enchant)) continue; // skipped (conflict/incompatible)
            int rarityOrdinal = enchant.getRarity().ordinal() + 1; // 1 = COMMON … 6 = MYTHIC
            cost += rarityOrdinal * level;
        }
        cost = Math.max(1, Math.min(cost, 40));

        event.setResult(result);
        anvil.setRepairCost(cost);
    }
}

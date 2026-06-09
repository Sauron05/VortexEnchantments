package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantManager;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MiningListener implements Listener {

    private final VortexEnchantments plugin;
    private final EnchantManager manager;

    public MiningListener(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        // Additional mining-specific logic beyond EnchantListener can go here
        // e.g., Core Tap counter tracking
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (ItemUtil.isAir(item)) return;

        // Increment generic block-break counter for enchants that need it
        Map<VortexEnchant, Integer> enchants = manager.getEnchants(item);
        if (!enchants.isEmpty()) {
            plugin.getPlayerDataManager().incrementInt(player.getUniqueId(), "blocks_mined");
        }
    }
}

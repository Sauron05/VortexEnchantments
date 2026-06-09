package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantManager;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class FishingListener implements Listener {

    @SuppressWarnings("unused")
    private final VortexEnchantments plugin;
    private final EnchantManager manager;

    public FishingListener(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack rod = player.getInventory().getItemInMainHand();
        if (rod.getType() != org.bukkit.Material.FISHING_ROD) {
            rod = player.getInventory().getItemInOffHand();
        }
        if (ItemUtil.isAir(rod) || rod.getType() != org.bukkit.Material.FISHING_ROD) return;

        Map<VortexEnchant, Integer> enchants = manager.getEnchants(rod);
        for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
            if (e.getKey().isEnabled()) {
                e.getKey().onFish(event, player, e.getValue());
            }
        }
    }
}

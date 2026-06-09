package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantManager;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ShieldListener implements Listener {

    @SuppressWarnings("unused")
    private final VortexEnchantments plugin;
    private final EnchantManager manager;

    public ShieldListener(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onShieldUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.OFF_HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (offhand.getType() != Material.SHIELD) return;

        Map<VortexEnchant, Integer> enchants = manager.getEnchants(offhand);
        for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
            if (e.getKey().isEnabled()) {
                e.getKey().onInteract(event, player, offhand, e.getValue());
            }
        }
    }
}

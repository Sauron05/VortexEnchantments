package com.vortexrpg.enchantments.gui;

import com.vortexrpg.enchantments.VortexEnchantments;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * GUI click handler for the Souls Shop.
 */
public class SoulsShopGUI implements Listener {

    private static final String GUI_TITLE_PREFIX = "§5§lSouls Shop";

    private final VortexEnchantments plugin;

    public SoulsShopGUI(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().startsWith(GUI_TITLE_PREFIX)) return;
        event.setCancelled(true);

        int slot = event.getRawSlot();
        plugin.getSoulsManager().handleShopClick(player, slot);
    }
}

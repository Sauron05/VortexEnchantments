package com.vortexrpg.enchantments.listener;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Handles right-click interactions for Mystery Scrolls and Extractor Scrolls.
 */
public class ScrollListener implements Listener {

    private final VortexEnchantments plugin;

    public ScrollListener(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) return;

        // Mystery Scroll — right click to open spin GUI
        if (plugin.getMysteryScroll().isScroll(item)) {
            event.setCancelled(true);
            EnchantRarity fixedTier = plugin.getMysteryScroll().getFixedTier(item);

            // Consume the scroll
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }

            plugin.getMysteryScrollGUI().open(player, fixedTier);
            return;
        }

        // Extractor Scroll — right click while holding enchanted item in main hand
        // The extractor should be in off-hand, with the enchanted item in main hand
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (plugin.getExtractorScroll().isExtractor(offHand)) {
            // Main hand must have an enchanted item (not a scroll or book)
            if (item.getType() == Material.PAPER || item.getType() == Material.ENCHANTED_BOOK) return;

            Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(item);
            if (enchants.isEmpty()) {
                player.sendMessage("§cThat item has no VortexEnchantments to extract.");
                return;
            }

            event.setCancelled(true);
            plugin.getExtractorGUI().open(player, item);
        }
    }
}

package com.vortexrpg.enchantments.api;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantManager;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Public API for other plugins to interact with VortexEnchantments.
 *
 * Usage:
 *   VortexEnchantAPI api = (VortexEnchantAPI) Bukkit.getServer().getPluginManager()
 *       .getPlugin("VortexEnchantments");
 *   // OR
 *   VortexEnchantAPI api = VortexEnchantments.getInstance().getApi();
 */
public class VortexEnchantAPI {

    private final VortexEnchantments plugin;
    private final EnchantManager manager;

    public VortexEnchantAPI(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    /**
     * Returns the enchantment level of the given enchant on the given item.
     * Returns 0 if the item doesn't carry the enchant.
     */
    public int getEnchantLevel(ItemStack item, String enchantId) {
        return manager.getLevel(item, enchantId);
    }

    /**
     * Returns true if the item has the given enchant at any level.
     */
    public boolean hasEnchant(ItemStack item, String enchantId) {
        return getEnchantLevel(item, enchantId) > 0;
    }

    /**
     * Apply an enchant to an item at the given level. If already present, overwrites.
     */
    public void applyEnchant(ItemStack item, String enchantId, int level) {
        VortexEnchant enchant = manager.getById(enchantId);
        if (enchant != null) manager.applyEnchant(item, enchant, level);
    }

    /**
     * Remove an enchant from an item.
     */
    public void removeEnchant(ItemStack item, String enchantId) {
        VortexEnchant enchant = manager.getById(enchantId);
        if (enchant != null) manager.removeEnchant(item, enchant);
    }

    /**
     * Returns a map of all VortexEnchant → level on the item.
     */
    public Map<VortexEnchant, Integer> getAllEnchants(ItemStack item) {
        return manager.getEnchants(item);
    }

    /**
     * Returns a list of all registered enchantment IDs.
     */
    public List<String> getAllEnchantIds() {
        return manager.getAll().stream().map(VortexEnchant::getId).toList();
    }

    /**
     * Returns the VortexEnchant object for a given ID, or null if not found.
     */
    public VortexEnchant getEnchant(String id) {
        return manager.getById(id);
    }

    /**
     * Returns true if an enchant with this ID is registered.
     */
    public boolean isRegistered(String enchantId) {
        return manager.getById(enchantId) != null;
    }

    /**
     * Check if a player has a cooldown active for a specific enchantment.
     */
    public boolean isOnCooldown(Player player, String enchantId) {
        return plugin.getCooldownManager().isOnCooldown(player, enchantId);
    }

    /**
     * Returns remaining cooldown in milliseconds for a player+enchant pair.
     */
    public long getRemainingCooldown(Player player, String enchantId) {
        return plugin.getCooldownManager().getRemainingMillis(player, enchantId);
    }

    /**
     * Create an enchanted book item for the given enchant+level.
     */
    public ItemStack createEnchantedBook(String enchantId, int level) {
        VortexEnchant enchant = manager.getById(enchantId);
        if (enchant == null) return null;
        return manager.createEnchantedBook(enchant, level);
    }
}

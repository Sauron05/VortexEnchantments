package com.vortexrpg.enchantments.item;

import com.vortexrpg.enchantments.VortexEnchantments;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Holy White Scroll — permanent, multi-purpose item protection (never consumed).
 * 
 * Superior to AE implementation:
 *   - Saves counter: tracks how many times this scroll has saved the item (shown in lore)
 *   - Death protection: also prevents the item from dropping on death (configurable)
 *   - Enchant destroy protection: permanent (unlike White Scroll which gets consumed)
 *   - Visual lore indicator with save history
 *   - Can be removed with a Black Scroll (configurable)
 *   - Glows gold (enchant shimmer) for visual distinction
 */
@SuppressWarnings("deprecation")
public class HolyWhiteScroll {

    private final NamespacedKey scrollKey;
    private final NamespacedKey protectedKey;
    private final NamespacedKey saveCountKey;
    private final VortexEnchantments plugin;

    public HolyWhiteScroll(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.scrollKey = new NamespacedKey(plugin, "holy_white_scroll");
        this.protectedKey = new NamespacedKey(plugin, "holy_scroll_protected");
        this.saveCountKey = new NamespacedKey(plugin, "holy_save_count");
    }

    public ItemStack create(int amount) {
        ItemStack item = new ItemStack(Material.PAPER, Math.min(amount, 64));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName("§e§l✦ Holy White Scroll ✦");
        List<String> lore = new ArrayList<>();
        lore.add("§8Sacred Protection Scroll");
        lore.add("§8§m                              ");
        lore.add("");
        lore.add("§7Drag onto an item to grant it");
        lore.add("§a§lPERMANENT §7protection from:");
        lore.add("");
        lore.add("  §e▸ §7Enchant destruction on failure");
        if (isDeathProtectionEnabled()) {
            lore.add("  §e▸ §7Item dropping on death");
        }
        lore.add("");
        lore.add("§7Unlike White Scroll, Holy is");
        lore.add("§6§lNEVER consumed §7— protects forever.");
        lore.add("");
        lore.add("§7Each save is tracked and displayed");
        lore.add("§7on the item's lore.");
        lore.add("");
        lore.add("§e§l✦ Infinite Protection ✦");
        lore.add("§8§m                              ");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        meta.getPersistentDataContainer().set(scrollKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isHolyWhiteScroll(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(scrollKey, PersistentDataType.BOOLEAN);
    }

    /**
     * Apply holy protection to an item.
     */
    public boolean applyProtection(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (isProtected(item)) return false;
        var meta = item.getItemMeta();
        if (meta == null) return false;

        meta.getPersistentDataContainer().set(protectedKey, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(saveCountKey, PersistentDataType.INTEGER, 0);
        updateProtectionLore(meta, 0);
        item.setItemMeta(meta);
        return true;
    }

    /**
     * Record a save event (the holy scroll blocked destruction).
     * Increments the counter and updates the lore.
     */
    public void recordSave(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;
        var meta = item.getItemMeta();
        if (meta == null) return;

        int count = getSaveCount(item) + 1;
        meta.getPersistentDataContainer().set(saveCountKey, PersistentDataType.INTEGER, count);
        updateProtectionLore(meta, count);
        item.setItemMeta(meta);
    }

    /**
     * Remove holy protection from an item.
     */
    public boolean removeProtection(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!isProtected(item)) return false;
        var meta = item.getItemMeta();
        if (meta == null) return false;

        meta.getPersistentDataContainer().remove(protectedKey);
        meta.getPersistentDataContainer().remove(saveCountKey);

        // Clean lore
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.removeIf(line -> line.contains("Holy Protected") || line.contains("Saves:"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return true;
    }

    private void updateProtectionLore(ItemMeta meta, int saveCount) {
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.removeIf(line -> line.contains("Holy Protected") || line.contains("Saves:"));

        String shield = "§e§l✦ Holy Protected §7(Permanent)";
        if (saveCount > 0) {
            // Visual save indicator: stars for each save
            StringBuilder saves = new StringBuilder("§7  Saves: ");
            for (int i = 0; i < Math.min(saveCount, 10); i++) saves.append("§e⭐");
            if (saveCount > 10) saves.append(" §7+").append(saveCount - 10);
            lore.add(shield);
            lore.add(saves.toString());
        } else {
            lore.add(shield);
        }
        meta.setLore(lore);
    }

    public boolean isProtected(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(protectedKey, PersistentDataType.BOOLEAN);
    }

    public int getSaveCount(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return 0;
        var meta = item.getItemMeta();
        if (meta == null) return 0;
        var pdc = meta.getPersistentDataContainer();
        return pdc.has(saveCountKey, PersistentDataType.INTEGER)
            ? pdc.get(saveCountKey, PersistentDataType.INTEGER) : 0;
    }

    /**
     * Check if death protection is enabled in config.
     */
    public boolean isDeathProtectionEnabled() {
        return plugin.getConfig().getBoolean("holy-white-scroll.death-protection", true);
    }

    public NamespacedKey getScrollKey() { return scrollKey; }
    public NamespacedKey getProtectedKey() { return protectedKey; }
}

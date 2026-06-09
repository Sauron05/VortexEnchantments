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
 * White Scroll — protects an item from being destroyed on failed enchant application.
 * Black Scroll — removes a random enchant from an item, returning it as a book.
 */
@SuppressWarnings("deprecation")
public class ProtectionScrolls {

    private final NamespacedKey whiteScrollKey;
    private final NamespacedKey blackScrollKey;
    private final NamespacedKey whiteScrollProtectedKey;

    public ProtectionScrolls(VortexEnchantments plugin) {
        this.whiteScrollKey = new NamespacedKey(plugin, "white_scroll");
        this.blackScrollKey = new NamespacedKey(plugin, "black_scroll");
        this.whiteScrollProtectedKey = new NamespacedKey(plugin, "white_scroll_protected");
    }

    // ─── White Scroll ────────────────────────────────────────────────────────

    public ItemStack createWhiteScroll() {
        return createWhiteScroll(1);
    }

    public ItemStack createWhiteScroll(int amount) {
        ItemStack item = new ItemStack(Material.PAPER, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName("§f§lWhite Scroll");
        List<String> lore = new ArrayList<>();
        lore.add("§8Protection Scroll");
        lore.add("");
        lore.add("§7Drag onto an item to protect");
        lore.add("§7it from being §cdestroyed§7 on");
        lore.add("§7a failed enchantment application.");
        lore.add("");
        lore.add("§a§lOne-time use protection");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(whiteScrollKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isWhiteScroll(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(whiteScrollKey, PersistentDataType.BOOLEAN);
    }

    /** Apply white scroll protection to an item. Adds lore + PDC flag. */
    public void applyProtection(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;
        var meta = item.getItemMeta();
        if (meta == null) return;
        if (isProtected(item)) return;

        meta.getPersistentDataContainer().set(whiteScrollProtectedKey, PersistentDataType.BOOLEAN, true);
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add("§f§l✦ Protected §7(White Scroll)");
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /** Check if an item has white scroll protection. */
    public boolean isProtected(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(whiteScrollProtectedKey, PersistentDataType.BOOLEAN);
    }

    /** Consume protection (one-time use). Returns true if was protected. */
    public boolean consumeProtection(ItemStack item) {
        if (!isProtected(item)) return false;
        var meta = item.getItemMeta();
        if (meta == null) return false;
        meta.getPersistentDataContainer().remove(whiteScrollProtectedKey);
        if (meta.getLore() != null) {
            List<String> lore = new ArrayList<>(meta.getLore());
            lore.removeIf(l -> l.contains("Protected") && l.contains("White Scroll"));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return true;
    }

    // ─── Black Scroll ────────────────────────────────────────────────────────

    public ItemStack createBlackScroll() {
        return createBlackScroll(1);
    }

    public ItemStack createBlackScroll(int amount) {
        ItemStack item = new ItemStack(Material.PAPER, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName("§0§lBlack Scroll");
        List<String> lore = new ArrayList<>();
        lore.add("§8Extraction Scroll");
        lore.add("");
        lore.add("§7Drag onto an enchanted item");
        lore.add("§7to remove a §crandom§7 enchant");
        lore.add("§7and receive it as a book.");
        lore.add("");
        lore.add("§c§lWarning: Random enchant removed!");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(blackScrollKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isBlackScroll(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(blackScrollKey, PersistentDataType.BOOLEAN);
    }

    public NamespacedKey getWhiteScrollKey() { return whiteScrollKey; }
    public NamespacedKey getBlackScrollKey() { return blackScrollKey; }
    public NamespacedKey getWhiteScrollProtectedKey() { return whiteScrollProtectedKey; }
}

package com.vortexrpg.enchantments.item;

import com.vortexrpg.enchantments.VortexEnchantments;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Creates and identifies Extractor Scroll items.
 * Used to pull an enchantment off an item back into a book.
 */
@SuppressWarnings("deprecation")
public class ExtractorScroll {

    private static final String DISPLAY_NAME = "§c§l✦ Extractor Scroll ✦";
    private final NamespacedKey extractorKey;

    public ExtractorScroll(VortexEnchantments plugin) {
        this.extractorKey = new NamespacedKey(plugin, "extractor_scroll");
    }

    public ItemStack create() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(DISPLAY_NAME);
        meta.getPersistentDataContainer().set(extractorKey, PersistentDataType.BYTE, (byte) 1);
        meta.setLore(List.of(
            "§7Right-click while holding an enchanted item",
            "§7to extract one enchantment into a book.",
            "",
            "§cSuccess chance scales with rarity:",
            "§f  Common: §a90%  §aUncommon: §a80%",
            "§9  Rare: §e60%  §5  Epic: §e40%",
            "§6  Legendary: §c25%  §c  Mythic: §c15%",
            "",
            "§4§oFailure destroys the enchantment!"
        ));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isExtractor(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        Byte val = meta.getPersistentDataContainer().get(extractorKey, PersistentDataType.BYTE);
        return val != null && val == 1;
    }

    public NamespacedKey getKey() {
        return extractorKey;
    }
}

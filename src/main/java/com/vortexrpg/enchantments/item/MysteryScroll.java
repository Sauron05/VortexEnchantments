package com.vortexrpg.enchantments.item;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Creates and identifies Mystery Scroll items.
 * Right-clicking opens an animated spin GUI.
 */
@SuppressWarnings("deprecation")
public class MysteryScroll {

    private static final String DISPLAY_NAME = "§5§l✦ Mystery Enchant Scroll ✦";
    private final NamespacedKey scrollKey;
    private final VortexEnchantments plugin;

    public MysteryScroll(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.scrollKey = new NamespacedKey(plugin, "mystery_scroll");
    }

    public ItemStack create() {
        return create(null);
    }

    /**
     * Create a mystery scroll. If tier is null, it pulls from all rarities.
     */
    public ItemStack create(EnchantRarity fixedTier) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(DISPLAY_NAME);
        meta.getPersistentDataContainer().set(scrollKey, PersistentDataType.BYTE, (byte) 1);

        if (fixedTier != null) {
            NamespacedKey tierKey = new NamespacedKey(plugin, "scroll_tier");
            meta.getPersistentDataContainer().set(tierKey, PersistentDataType.STRING, fixedTier.name());
            meta.setLore(List.of(
                "§7Right-click to reveal a random enchantment!",
                "§7Guaranteed: " + fixedTier.getColor() + fixedTier.getDisplayName(),
                "",
                "§8§oA shimmering scroll pulses with power..."
            ));
        } else {
            meta.setLore(List.of(
                "§7Right-click to reveal a random enchantment!",
                "",
                "§8§oA shimmering scroll pulses with power..."
            ));
        }

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isScroll(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        Byte val = meta.getPersistentDataContainer().get(scrollKey, PersistentDataType.BYTE);
        return val != null && val == 1;
    }

    public EnchantRarity getFixedTier(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        NamespacedKey tierKey = new NamespacedKey(plugin, "scroll_tier");
        String tier = meta.getPersistentDataContainer().get(tierKey, PersistentDataType.STRING);
        if (tier == null) return null;
        try {
            return EnchantRarity.valueOf(tier);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public NamespacedKey getKey() {
        return scrollKey;
    }
}

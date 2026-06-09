package com.vortexrpg.enchantments.item;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Enchant Dust — 6 tiers matching rarity. Boosts success rate when applied to an enchanted book.
 * Can also be applied to an item directly via the Alchemist.
 */
@SuppressWarnings("deprecation")
public class DustItem {

    private final VortexEnchantments plugin;
    private final NamespacedKey dustKey;
    private final NamespacedKey dustTierKey;

    // Success bonus per tier (configurable via config)
    private static final int[] DEFAULT_BONUS = {5, 10, 15, 20, 25, 30};

    public DustItem(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.dustKey = new NamespacedKey(plugin, "enchant_dust");
        this.dustTierKey = new NamespacedKey(plugin, "dust_tier");
    }

    public ItemStack create(EnchantRarity tier) {
        return create(tier, 1);
    }

    public ItemStack create(EnchantRarity tier, int amount) {
        ItemStack item = new ItemStack(Material.GLOWSTONE_DUST, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String tierName = tier.getDisplayName();
        meta.setDisplayName(tier.getColor() + "§l" + tierName + " Enchant Dust");
        List<String> lore = new ArrayList<>();
        lore.add("§8Enchant Dust");
        lore.add("");
        lore.add("§7Tier: " + tier.getColor() + tierName);
        lore.add("§7Success Bonus: §a+" + getBonus(tier) + "%");
        lore.add("");
        lore.add("§eApply to an enchanted book");
        lore.add("§eto boost its success rate.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(dustKey, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(dustTierKey, PersistentDataType.INTEGER, tier.ordinal());
        item.setItemMeta(meta);
        return item;
    }

    public boolean isDust(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(dustKey, PersistentDataType.BOOLEAN);
    }

    public EnchantRarity getTier(ItemStack item) {
        if (!isDust(item)) return null;
        var meta = item.getItemMeta();
        if (meta == null) return null;
        Integer ordinal = meta.getPersistentDataContainer().get(dustTierKey, PersistentDataType.INTEGER);
        if (ordinal == null || ordinal < 0 || ordinal >= EnchantRarity.values().length) return EnchantRarity.COMMON;
        return EnchantRarity.values()[ordinal];
    }

    public int getBonus(EnchantRarity tier) {
        int idx = tier.ordinal();
        return plugin.getConfig().getInt("dust.bonus-per-tier." + tier.name().toLowerCase(), DEFAULT_BONUS[idx]);
    }

    public NamespacedKey getDustKey() { return dustKey; }
    public NamespacedKey getDustTierKey() { return dustTierKey; }
}

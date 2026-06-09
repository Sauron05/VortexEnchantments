package com.vortexrpg.enchantments.util;

import com.vortexrpg.enchantments.VortexEnchantments;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Utility for working with ItemStacks and PDC in the context of VortexEnchantments.
 */
@SuppressWarnings("deprecation") // Legacy Spigot API (getLore/setLore) intentional for broad compat
public final class ItemUtil {

    private ItemUtil() {}

    public static boolean isAir(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /** Get the held main-hand item of a player (null-safe). */
    public static ItemStack getMainHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    /** Get the held off-hand item of a player (null-safe). */
    public static ItemStack getOffHand(Player player) {
        return player.getInventory().getItemInOffHand();
    }

    /** Returns all equipped armor pieces including main hand. */
    public static List<ItemStack> getEquipped(Player player) {
        List<ItemStack> items = new ArrayList<>();
        EntityEquipment eq = player.getEquipment();
        if (eq == null) return items;
        items.add(eq.getItemInMainHand());
        items.add(eq.getItemInOffHand());
        if (eq.getHelmet() != null) items.add(eq.getHelmet());
        if (eq.getChestplate() != null) items.add(eq.getChestplate());
        if (eq.getLeggings() != null) items.add(eq.getLeggings());
        if (eq.getBoots() != null) items.add(eq.getBoots());
        items.removeIf(ItemUtil::isAir);
        return items;
    }

    /** Store an integer in an item's PDC. */
    public static void setPDCInt(ItemStack item, NamespacedKey key, int value) {
        if (isAir(item)) return;
        var meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }

    /** Get an integer from an item's PDC (default 0). */
    public static int getPDCInt(ItemStack item, NamespacedKey key) {
        if (isAir(item)) return 0;
        var meta = item.getItemMeta();
        if (meta == null) return 0;
        Integer val = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        return val != null ? val : 0;
    }

    /** Store a string in an item's PDC. */
    public static void setPDCString(ItemStack item, NamespacedKey key, String value) {
        if (isAir(item)) return;
        var meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    /** Get a string from an item's PDC. */
    public static String getPDCString(ItemStack item, NamespacedKey key) {
        if (isAir(item)) return "";
        var meta = item.getItemMeta();
        if (meta == null) return "";
        String val = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return val != null ? val : "";
    }

    /** Store a double in an item's PDC. */
    public static void setPDCDouble(ItemStack item, NamespacedKey key, double value) {
        if (isAir(item)) return;
        var meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
    }

    /** Get a double from an item's PDC. */
    public static double getPDCDouble(ItemStack item, NamespacedKey key, double def) {
        if (isAir(item)) return def;
        var meta = item.getItemMeta();
        if (meta == null) return def;
        Double val = meta.getPersistentDataContainer().get(key, PersistentDataType.DOUBLE);
        return val != null ? val : def;
    }

    /** Append a line to an item's lore. */
    public static void addLore(ItemStack item, String line) {
        if (isAir(item)) return;
        var meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add(line);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /** Check if main-hand tool is a sword-type. */
    public static boolean holdingSword(Player player) {
        return switch (getMainHand(player).getType()) {
            case WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> true;
            default -> false;
        };
    }

    /** Check if item is a food item. */
    public static boolean isFood(ItemStack item) {
        if (isAir(item)) return false;
        return item.getType().isEdible();
    }

    /**
     * Damage an item in-hand by a specified amount using standard Bukkit durability.
     */
    public static void damageItem(Player player, ItemStack item, int amount) {
        player.damageItemStack(item, amount);
    }

    /**
     * Returns a compact string for all VortexEnchants on an item.
     */
    public static String describeEnchants(ItemStack item) {
        var enchants = VortexEnchantments.getInstance().getEnchantManager().getEnchants(item);
        if (enchants.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for (var e : enchants.entrySet()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(e.getKey().getDisplayName()).append(" ").append(e.getValue());
        }
        return sb.toString();
    }
}

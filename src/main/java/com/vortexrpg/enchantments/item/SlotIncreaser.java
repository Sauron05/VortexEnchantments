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
import java.util.concurrent.ThreadLocalRandom;

/**
 * Slot Increaser — tiered consumable scrolls that increase max enchantment slots on an item.
 * 
 * Superior to AE implementation:
 *   - 3 tiers: I (+1, 100% success), II (+2, 70% success), III (+3, 40% success)
 *   - Visual progress bar on item lore showing filled vs max slots
 *   - Success/failure mechanic with feedback
 *   - Each tier uses a different material for visual distinction
 *   - Configurable max bonus and success rates
 */
@SuppressWarnings("deprecation")
public class SlotIncreaser {

    private final NamespacedKey scrollKey;
    private final NamespacedKey scrollTierKey;
    private final NamespacedKey slotBonusKey;
    private final VortexEnchantments plugin;

    public SlotIncreaser(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.scrollKey = new NamespacedKey(plugin, "slot_increaser");
        this.scrollTierKey = new NamespacedKey(plugin, "slot_increaser_tier");
        this.slotBonusKey = new NamespacedKey(plugin, "enchant_slot_bonus");
    }

    /**
     * Create a Slot Increaser of a given tier (1, 2, or 3).
     */
    public ItemStack create(int amount, int tier) {
        tier = Math.max(1, Math.min(3, tier));
        Material mat = switch (tier) {
            case 1 -> Material.PAPER;
            case 2 -> Material.MAP;
            case 3 -> Material.ENCHANTED_BOOK;
            default -> Material.PAPER;
        };
        String tierName = switch (tier) {
            case 1 -> "§e§lSlot Increaser I";
            case 2 -> "§6§lSlot Increaser II";
            case 3 -> "§c§lSlot Increaser III";
            default -> "§e§lSlot Increaser I";
        };
        String tierColor = switch (tier) {
            case 1 -> "§e";
            case 2 -> "§6";
            case 3 -> "§c";
            default -> "§e";
        };
        int slotsGiven = tier;
        int successRate = getSuccessRate(tier);

        ItemStack item = new ItemStack(mat, Math.min(amount, 64));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(tierName);
        List<String> lore = new ArrayList<>();
        lore.add("§8Enchantment Scroll");
        lore.add("§8§m                              ");
        lore.add("");
        lore.add("§7Drag onto an item to increase");
        lore.add("§7the maximum enchant slots by " + tierColor + "+" + slotsGiven + "§7.");
        lore.add("");
        lore.add("§7Success Rate: " + getSuccessColor(successRate) + successRate + "%");
        lore.add("");
        if (tier == 3) {
            lore.add("§c§l⚠ High risk — may fail!");
        }
        lore.add(tierColor + "§l✦ Tier " + toRoman(tier) + " §8| §7One-time use");
        lore.add("§8§m                              ");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if (tier >= 2) meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        meta.getPersistentDataContainer().set(scrollKey, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(scrollTierKey, PersistentDataType.INTEGER, tier);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Create with default tier 1 (backwards compatibility).
     */
    public ItemStack create(int amount) {
        return create(amount, 1);
    }

    public boolean isSlotIncreaser(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        var meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(scrollKey, PersistentDataType.BOOLEAN);
    }

    /**
     * Get the tier of a Slot Increaser scroll (1, 2, or 3).
     */
    public int getScrollTier(ItemStack item) {
        if (item == null) return 1;
        var meta = item.getItemMeta();
        if (meta == null) return 1;
        var pdc = meta.getPersistentDataContainer();
        if (pdc.has(scrollTierKey, PersistentDataType.INTEGER)) {
            return pdc.get(scrollTierKey, PersistentDataType.INTEGER);
        }
        return 1;
    }

    /**
     * Attempt to apply the slot increase. Returns a result indicating success or failure.
     */
    public SlotResult applySlotIncrease(ItemStack item, ItemStack scroll) {
        if (item == null || item.getType() == Material.AIR) return SlotResult.INVALID;
        var meta = item.getItemMeta();
        if (meta == null) return SlotResult.INVALID;

        int maxBonus = plugin.getConfig().getInt("slot-increaser.max-bonus", 8);
        int currentBonus = getSlotBonus(item);
        int tier = getScrollTier(scroll);
        int slotsToAdd = tier;

        // Check if already at max
        int newBonus = Math.min(currentBonus + slotsToAdd, maxBonus);
        if (currentBonus >= maxBonus) return SlotResult.MAX_REACHED;

        // Roll success chance
        int successRate = getSuccessRate(tier);
        if (ThreadLocalRandom.current().nextInt(100) >= successRate) {
            return SlotResult.FAILED;
        }

        // Success — apply the bonus
        meta.getPersistentDataContainer().set(slotBonusKey, PersistentDataType.INTEGER, newBonus);

        // Update lore with visual progress bar
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.removeIf(line -> line.contains("Enchant Slots") || line.contains("▰") || line.contains("▱"));
        lore.add(buildSlotBar(newBonus, maxBonus));
        meta.setLore(lore);

        item.setItemMeta(meta);
        return new SlotResult(true, slotsToAdd, newBonus);
    }

    /**
     * Legacy method — applies tier 1 always succeeds.
     */
    public boolean applySlotIncrease(ItemStack item) {
        SlotResult result = applySlotIncrease(item, create(1, 1));
        return result.success;
    }

    /**
     * Build a visual progress bar for enchant slots.
     * Example: §e§l✦ Slots: ▰▰▰▱▱▱▱▱ §e(3/8)
     */
    private String buildSlotBar(int current, int max) {
        StringBuilder bar = new StringBuilder("§e§l✦ §eSlots: ");
        for (int i = 0; i < max; i++) {
            if (i < current) {
                bar.append("§a▰");
            } else {
                bar.append("§8▱");
            }
        }
        bar.append(" §e(").append(current).append("/").append(max).append(")");
        return bar.toString();
    }

    public int getSlotBonus(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return 0;
        var meta = item.getItemMeta();
        if (meta == null) return 0;
        var pdc = meta.getPersistentDataContainer();
        if (pdc.has(slotBonusKey, PersistentDataType.INTEGER)) {
            return pdc.get(slotBonusKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

    private int getSuccessRate(int tier) {
        return plugin.getConfig().getInt("slot-increaser.success-rate.tier-" + tier, switch (tier) {
            case 1 -> 100;
            case 2 -> 70;
            case 3 -> 40;
            default -> 100;
        });
    }

    private String getSuccessColor(int rate) {
        if (rate >= 80) return "§a";
        if (rate >= 50) return "§e";
        return "§c";
    }

    private String toRoman(int n) {
        return switch (n) { case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; default -> String.valueOf(n); };
    }

    public NamespacedKey getScrollKey() { return scrollKey; }
    public NamespacedKey getSlotBonusKey() { return slotBonusKey; }

    // ─── Result ──────────────────────────────────────────────────────────────

    public static class SlotResult {
        public static final SlotResult INVALID = new SlotResult(false, 0, 0);
        public static final SlotResult MAX_REACHED = new SlotResult(false, 0, -1);
        public static final SlotResult FAILED = new SlotResult(false, 0, -2);

        public final boolean success;
        public final int slotsAdded;
        public final int newTotal;

        public SlotResult(boolean success, int slotsAdded, int newTotal) {
            this.success = success;
            this.slotsAdded = slotsAdded;
            this.newTotal = newTotal;
        }

        public boolean isMaxReached() { return newTotal == -1; }
        public boolean isFailed() { return newTotal == -2; }
    }
}

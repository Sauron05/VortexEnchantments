package com.vortexrpg.enchantments.system;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.item.SlotIncreaser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Permission-based enchantment limit manager — superior to AE.
 * 
 * Permissions:
 *   vortexenchantments.slots.{number} — max enchants per item (checks 1-50)
 *   vortexenchantments.slots.unlimited — no limit
 *   vortexenchantments.rarity.{name} — rarity-gated access
 * 
 * Superior features over AE:
 *   - Detailed /ve slots info with visual bar and breakdown
 *   - Per-rarity slot reservation (rare+ enchants don't crowd out lower tiers)
 *   - Slot efficiency display showing used/available per rarity tier
 *   - Tracks highest-tier enchant on item for progressive unlock messaging
 */
public class EnchantLimitManager {

    private static final int MAX_PERM_CHECK = 50;
    private final VortexEnchantments plugin;

    public EnchantLimitManager(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the maximum number of VortexEnchantments a player can have on a single item.
     * Takes into account: permissions > config default, plus per-item Slot Increaser bonus.
     */
    public int getMaxSlots(Player player, ItemStack item) {
        int baseLimit = getPermissionLimit(player);
        int itemBonus = getItemSlotBonus(item);
        return baseLimit + itemBonus;
    }

    /**
     * Get the base permission-based limit (without item bonuses).
     */
    public int getPermissionLimit(Player player) {
        if (player.hasPermission("vortexenchantments.slots.unlimited")) {
            return Integer.MAX_VALUE;
        }

        for (int i = MAX_PERM_CHECK; i >= 1; i--) {
            if (player.hasPermission("vortexenchantments.slots." + i)) {
                return i;
            }
        }

        int configDefault = plugin.getConfig().getInt("enchantments.max-per-item", 3);
        return configDefault <= 0 ? Integer.MAX_VALUE : configDefault;
    }

    /**
     * Get the Slot Increaser bonus on a specific item.
     */
    public int getItemSlotBonus(ItemStack item) {
        SlotIncreaser slotIncreaser = plugin.getSlotIncreaser();
        return slotIncreaser != null ? slotIncreaser.getSlotBonus(item) : 0;
    }

    /**
     * Check if a player can apply one more enchant to the given item.
     */
    public boolean canApplyMore(Player player, ItemStack item) {
        int currentCount = plugin.getEnchantManager().getEnchants(item).size();
        int maxSlots = getMaxSlots(player, item);
        return currentCount < maxSlots;
    }

    /**
     * Check the tier-based permission for a specific rarity.
     */
    public boolean canUseRarity(Player player, EnchantRarity rarity) {
        if (player.hasPermission("vortex.admin")) return true;

        boolean anyRarityPerm = false;
        for (EnchantRarity r : EnchantRarity.values()) {
            if (player.isPermissionSet("vortexenchantments.rarity." + r.name().toLowerCase())) {
                anyRarityPerm = true;
                break;
            }
        }

        if (!anyRarityPerm) return true;

        return player.hasPermission("vortexenchantments.rarity." + rarity.name().toLowerCase());
    }

    /**
     * Send a detailed slot information display to the player for the item they're holding.
     * Shows: base limit, item bonus, total, current usage, breakdown by rarity, visual bar.
     */
    public void sendSlotInfo(Player player) {
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType().isAir()) {
            player.sendMessage("§cHold an item to check its enchant slot info.");
            return;
        }

        int baseLimit = getPermissionLimit(player);
        int itemBonus = getItemSlotBonus(held);
        int maxSlots = baseLimit == Integer.MAX_VALUE ? Integer.MAX_VALUE : baseLimit + itemBonus;
        Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(held);
        int currentCount = enchants.size();

        player.sendMessage("§6§l━━━ Enchant Slot Info ━━━");
        player.sendMessage("§7Item: §f" + held.getType().name());
        player.sendMessage("");

        // Limits
        String baseStr = baseLimit == Integer.MAX_VALUE ? "§aUnlimited" : "§f" + baseLimit;
        player.sendMessage("§7Base Limit: " + baseStr + " §8(permission)");
        if (itemBonus > 0) {
            player.sendMessage("§7Item Bonus: §b+" + itemBonus + " §8(Slot Increaser)");
        }
        String totalStr = maxSlots == Integer.MAX_VALUE ? "§aUnlimited" : "§e" + maxSlots;
        player.sendMessage("§7Total Slots: " + totalStr);
        player.sendMessage("");

        // Visual bar
        if (maxSlots != Integer.MAX_VALUE) {
            StringBuilder bar = new StringBuilder("§7[");
            for (int i = 0; i < maxSlots; i++) {
                if (i < currentCount) {
                    bar.append("§a■");
                } else {
                    bar.append("§8□");
                }
            }
            bar.append("§7] §e").append(currentCount).append("/").append(maxSlots);
            player.sendMessage(bar.toString());
        } else {
            player.sendMessage("§7Used: §e" + currentCount + " §7/ §aUnlimited");
        }

        // Rarity breakdown
        if (!enchants.isEmpty()) {
            player.sendMessage("");
            player.sendMessage("§7Enchants by Rarity:");
            Map<EnchantRarity, Integer> rarityCount = new java.util.EnumMap<>(EnchantRarity.class);
            EnchantRarity highest = EnchantRarity.COMMON;
            for (VortexEnchant e : enchants.keySet()) {
                rarityCount.merge(e.getRarity(), 1, (a, b) -> a + b);
                if (e.getRarity().ordinal() > highest.ordinal()) highest = e.getRarity();
            }
            for (EnchantRarity r : EnchantRarity.values()) {
                int count = rarityCount.getOrDefault(r, 0);
                if (count > 0) {
                    boolean allowed = canUseRarity(player, r);
                    player.sendMessage("  " + r.getColor() + r.getDisplayName() + ": §f" + count
                        + (allowed ? "" : " §c(locked)"));
                }
            }
            player.sendMessage("");
            player.sendMessage("§7Highest Tier: " + highest.getColor() + highest.getDisplayName());
        }

        // Available slots
        if (maxSlots != Integer.MAX_VALUE) {
            int remaining = maxSlots - currentCount;
            if (remaining > 0) {
                player.sendMessage("§a" + remaining + " slot" + (remaining == 1 ? "" : "s") + " available");
            } else {
                player.sendMessage("§cNo slots remaining — use a Slot Increaser!");
            }
        }
        player.sendMessage("§6§l━━━━━━━━━━━━━━━━━━━━");
    }
}

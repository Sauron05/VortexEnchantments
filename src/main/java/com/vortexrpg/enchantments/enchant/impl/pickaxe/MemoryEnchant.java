package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Memory: Track last 3 ore types mined. Mining matching 3-sequence gives +1 bonus drop. Break resets.
 */
public class MemoryEnchant extends VortexEnchant {
    public MemoryEnchant() { super("memory", "Memory", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (!mat.name().endsWith("_ORE")) return;
        UUID uuid = player.getUniqueId();
        // Shift sequence
        int newOrdinal = mat.ordinal() % 100;
        plugin.getPlayerDataManager().setInt(uuid, "memory_0", plugin.getPlayerDataManager().getInt(uuid, "memory_1"));
        plugin.getPlayerDataManager().setInt(uuid, "memory_1", plugin.getPlayerDataManager().getInt(uuid, "memory_2"));
        plugin.getPlayerDataManager().setInt(uuid, "memory_2", newOrdinal);
        // Check if all three match
        int m0 = plugin.getPlayerDataManager().getInt(uuid, "memory_0");
        int m1 = plugin.getPlayerDataManager().getInt(uuid, "memory_1");
        int m2 = plugin.getPlayerDataManager().getInt(uuid, "memory_2");
        if (m0 == m1 && m1 == m2 && m0 == newOrdinal) {
            int bonus = cfgi("bonus_drops", 1 + level - 1);
            for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
                ItemStack extra = drop.clone();
                extra.setAmount(bonus);
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), extra);
            }
            // Reset
            plugin.getPlayerDataManager().setInt(uuid, "memory_0", 0);
            plugin.getPlayerDataManager().setInt(uuid, "memory_1", 0);
            plugin.getPlayerDataManager().setInt(uuid, "memory_2", 0);
        }
    }

    @Override public String getDescription() { return "Mining the same ore 3 times in a row gives bonus drops."; }
    @Override public String getDescription(int level) {
        return "§7Mine same ore §e3×§7: §a+" + level + " bonus drop§7. Break sequence resets."; }
}

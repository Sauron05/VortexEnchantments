package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Deposit: Every 64 mined dirt gives 1 clay ball. */
public class DepositEnchant extends VortexEnchant {
    public DepositEnchant() { super("deposit", "Deposit", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getBlock().getType() != Material.DIRT && event.getBlock().getType() != Material.GRASS_BLOCK) return;
        int threshold = cfgi("blocks_per_reward", 64);
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "deposit_dirt_count") + 1;
        if (count >= threshold) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "deposit_dirt_count", 0);
            player.getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.CLAY_BALL));
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "deposit_dirt_count", count);
        }
    }

    @Override public String getDescription() { return "Every 64 dirt mined yields clay."; }
    @Override public String getDescription(int level) { return "§7Every §e64 dirt§7 mined: §b1 clay ball§7."; }
}

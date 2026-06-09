package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Tributary: Mining block adjacent to water has 15/20/25% chance to drop prismarine shard. */
public class TributaryEnchant extends VortexEnchant {
    private static final double[] CHANCE = {15, 20, 25};

    public TributaryEnchant() { super("tributary", "Tributary", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!hasAdjacentWater(event.getBlock())) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        event.getBlock().getWorld().dropItemNaturally(
            event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.PRISMARINE_SHARD));
    }

    private boolean hasAdjacentWater(Block b) {
        int[] dx = {1, -1, 0, 0, 0, 0};
        int[] dy = {0, 0, 1, -1, 0, 0};
        int[] dz = {0, 0, 0, 0, 1, -1};
        for (int i = 0; i < 6; i++) {
            if (b.getRelative(dx[i], dy[i], dz[i]).getType() == Material.WATER) return true;
        }
        return false;
    }

    @Override public String getDescription() { return "Mining near water may drop prismarine."; }
    @Override public String getDescription(int level) {
        return "§7Adjacent to water: §a" + (int)CHANCE[level-1] + "%§7 chance to drop §bprismarine shard§7."; }
}

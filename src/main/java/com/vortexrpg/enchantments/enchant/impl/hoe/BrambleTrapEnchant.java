package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Bramble Trap: Harvesting crops places sweet berry bushes around perimeter. */
public class BrambleTrapEnchant extends VortexEnchant {

    public BrambleTrapEnchant() { super("bramble_trap", "Bramble Trap", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getBlock().getBlockData() instanceof org.bukkit.block.data.Ageable)) return;
        double chance = cfg("chance", 15.0 + level * 10);
        if (!MathUtil.chance(chance)) return;
        int count = cfgi("bushes", level + 1);
        Block center = event.getBlock();
        int placed = 0;
        int[][] offsets = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,-1},{1,-1},{-1,1}};
        for (int[] off : offsets) {
            if (placed >= count) break;
            Block target = center.getRelative(off[0], 0, off[1]);
            Block below = target.getRelative(0, -1, 0);
            if (target.getType().isAir() && (below.getType() == Material.GRASS_BLOCK
                    || below.getType() == Material.DIRT || below.getType() == Material.FARMLAND)) {
                target.setType(Material.SWEET_BERRY_BUSH);
                placed++;
            }
        }
    }

    @Override public String getDescription() { return "Harvesting places thorny bushes around crops."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: §a" + (int)(15 + level * 10) + "%§7 chance for §c" + (level + 1) + "§7 bramble bushes."; }
}

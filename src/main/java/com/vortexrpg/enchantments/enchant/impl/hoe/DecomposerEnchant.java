package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Decomposer: Breaking plants fills nearby composters. */
public class DecomposerEnchant extends VortexEnchant {

    public DecomposerEnchant() { super("decomposer", "Decomposer", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (!mat.name().contains("LEAVES") && mat != Material.WHEAT && mat != Material.CARROTS
                && mat != Material.POTATOES && mat != Material.BEETROOTS && mat != Material.TALL_GRASS
                && mat != Material.SHORT_GRASS && mat != Material.FERN) return;
        int radius = cfgi("radius", 3 + level);
        Block center = event.getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 2; y++) {
                    Block b = center.getRelative(x, y, z);
                    if (b.getType() == Material.COMPOSTER) {
                        if (b.getBlockData() instanceof org.bukkit.block.data.Levelled lev) {
                            if (lev.getLevel() < lev.getMaximumLevel()) {
                                lev.setLevel(Math.min(lev.getMaximumLevel(), lev.getLevel() + 1));
                                b.setBlockData(lev);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Breaking plants fills nearby composters."; }
    @Override public String getDescription(int level) {
        return "§7Break plants: fill composters within §e" + (3 + level) + "§7 blocks."; }
}

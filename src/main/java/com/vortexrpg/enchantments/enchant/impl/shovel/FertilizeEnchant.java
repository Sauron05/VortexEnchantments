package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Fertilize: Digging dirt near saplings boosts their growth. */
public class FertilizeEnchant extends VortexEnchant {
    private static final double[] CHANCE = {10, 15, 20};

    public FertilizeEnchant() { super("fertilize", "Fertilize", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.DIRT && mat != Material.GRASS_BLOCK) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        int radius = cfgi("radius", 3);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = event.getBlock().getRelative(x, y, z);
                    if (b.getType().name().endsWith("_SAPLING")) {
                        // Bone-meal the sapling
                        b.applyBoneMeal(org.bukkit.block.BlockFace.UP);
                        return;
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Digging near saplings boosts growth."; }
    @Override public String getDescription(int level) {
        return "§7Dirt: §a" + (int) CHANCE[level - 1] + "%§7 to §2fertilize§7 nearest sapling."; }
}

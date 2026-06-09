package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Mulch: Mining dirt near crops has chance to bone-meal them. */
public class MulchEnchant extends VortexEnchant {
    private static final double[] CHANCE = {10, 15, 20};
    private static final Set<Material> SOIL = Set.of(Material.DIRT, Material.GRASS_BLOCK, Material.FARMLAND);

    public MulchEnchant() { super("mulch", "Mulch", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SOIL.contains(event.getBlock().getType())) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    Block b = event.getBlock().getRelative(x, y, z);
                    if (b.getBlockData() instanceof Ageable ageable) {
                        if (ageable.getAge() < ageable.getMaximumAge()) {
                            ageable.setAge(Math.min(ageable.getMaximumAge(), ageable.getAge() + 1));
                            b.setBlockData(ageable);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Mining dirt may boost nearby crop growth."; }
    @Override public String getDescription(int level) {
        return "§7Dirt: §a" + (int) CHANCE[level - 1] + "%§7 to §2bone-meal§7 nearest crop."; }
}

package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Random;

/** Hollow Earth: Mining has tiny chance to open a 5×5 pocket cave with 2-4 ores. */
public class HollowEarthEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.05, 0.10, 0.15};
    private static final Material[] CAVE_ORES = {Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.LAPIS_ORE};

    public HollowEarthEnchant() { super("hollow_earth", "Hollow Earth", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        Location center = event.getBlock().getLocation().clone().add(0, -3, 0);
        // Hollow out 5x5x4 cave
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y < 4; y++) {
                    Block b = center.clone().add(x, y, z).getBlock();
                    if (b.getType().isSolid()) b.setType(Material.AIR);
                }
            }
        }
        // Add ore nodes
        int oreCount = 2 + new Random().nextInt(3);
        for (int i = 0; i < oreCount; i++) {
            Location oreLoc = center.clone().add(
                new Random().nextInt(5) - 2, 1, new Random().nextInt(5) - 2);
            oreLoc.getBlock().setType(CAVE_ORES[new Random().nextInt(CAVE_ORES.length)]);
        }
    }

    @Override public String getDescription() { return "Mining may reveal a hidden pocket cave."; }
    @Override public String getDescription(int level) {
        return "§7Mining: §a" + CHANCE[level-1] + "%§7 chance to open a §e5×5§7 pocket cave with §b2-4 ores§7."; }
}

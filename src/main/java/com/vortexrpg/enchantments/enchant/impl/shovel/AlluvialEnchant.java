package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

/** Alluvial: Riverbed sand/gravel near water yields 5/8/10% gold nuggets/raw copper. */
public class AlluvialEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 10};
    private static final Material[] LOOT = {Material.GOLD_NUGGET, Material.RAW_COPPER};

    public AlluvialEnchant() { super("alluvial", "Alluvial", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.SAND && mat != Material.GRAVEL) return;
        boolean nearWater = false;
        for (int dx = -1; dx <= 1 && !nearWater; dx++) {
            for (int dz = -1; dz <= 1 && !nearWater; dz++) {
                if (event.getBlock().getRelative(dx, 0, dz).getType() == Material.WATER) nearWater = true;
            }
        }
        if (!nearWater) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        Material drop = LOOT[new Random().nextInt(LOOT.length)];
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(drop));
    }

    @Override public String getDescription() { return "Riverbed sand/gravel near water yields precious metals."; }
    @Override public String getDescription(int level) {
        return "§7Sand/gravel near water: §a" + (int)CHANCE[level-1] + "%§7 to drop §6gold nugget§7 or §7raw copper§7."; }
}

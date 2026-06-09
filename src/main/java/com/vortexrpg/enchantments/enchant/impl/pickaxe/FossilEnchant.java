package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Fossil: Stone/deepslate mining has small chance to drop a rare fossil item. */
public class FossilEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.5, 1.0, 2.0};
    private static final Material[] FOSSIL_MATS = {Material.BONE, Material.AMETHYST_SHARD, Material.QUARTZ};

    public FossilEnchant() { super("fossil", "Fossil", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.STONE && mat != Material.DEEPSLATE && mat != Material.COBBLED_DEEPSLATE) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        Material fossilMat = FOSSIL_MATS[(int)(Math.random() * FOSSIL_MATS.length)];
        loc.getWorld().dropItemNaturally(loc, new ItemStack(fossilMat));
    }

    @Override public String getDescription() { return "Mining stone may reveal ancient fossils."; }
    @Override public String getDescription(int level) {
        return "§7Stone/deepslate: §a" + CHANCE[level-1] + "%§7 chance to find an ancient fossil."; }
}

package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

/** Tiller: Any dig has 2/3/5% chance to drop a random seed. */
public class TillerEnchant extends VortexEnchant {
    private static final double[] CHANCE = {2, 3, 5};
    private static final org.bukkit.Material[] SEEDS = {
        org.bukkit.Material.WHEAT_SEEDS, org.bukkit.Material.CARROT,
        org.bukkit.Material.POTATO, org.bukkit.Material.BEETROOT_SEEDS
    };

    public TillerEnchant() { super("tiller", "Tiller", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        org.bukkit.Material seed = SEEDS[new Random().nextInt(SEEDS.length)];
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(seed));
    }

    @Override public String getDescription() { return "Digging may yield random seeds."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)CHANCE[level-1] + "%§7 chance per dig to drop a §2random seed§7."; }
}

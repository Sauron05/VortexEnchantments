package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Hybrid: 2/3/5% chance on harvest to receive a seed of a different crop type. */
public class HybridEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.02, 0.03, 0.05};
    private static final Material[] SEEDS = {
        Material.WHEAT_SEEDS, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS,
        Material.BEETROOT_SEEDS, Material.CARROT, Material.POTATO
    };

    public HybridEnchant() { super("hybrid", "Hybrid", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onHarvest(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            Material seed = SEEDS[(int)(Math.random() * SEEDS.length)];
            player.getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(seed));
        }
    }

    @Override public String getDescription() { return "Harvesting may yield a foreign crop seed."; }
    @Override public String getDescription(int level) {
        return "§7" + (int)(CHANCE[level-1]*100) + "§a%§7 to get a random crop seed on harvest."; }
}

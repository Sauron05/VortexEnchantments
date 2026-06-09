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

/** Soil Sampler: Mining dirt in different biomes gives biome-themed loot. */
public class SoilSamplerEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 12};

    public SoilSamplerEnchant() { super("soil_sampler", "Soil Sampler", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.DIRT && mat != Material.GRASS_BLOCK && mat != Material.SAND) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        String biome = event.getBlock().getBiome().getKey().value();
        Material drop;
        if (biome.contains("DESERT") || biome.contains("BADLANDS")) {
            drop = Material.GOLD_NUGGET;
        } else if (biome.contains("JUNGLE")) {
            drop = Material.COCOA_BEANS;
        } else if (biome.contains("TAIGA") || biome.contains("SNOWY")) {
            drop = Material.SWEET_BERRIES;
        } else if (biome.contains("OCEAN") || biome.contains("RIVER")) {
            drop = Material.PRISMARINE_SHARD;
        } else {
            drop = Material.WHEAT_SEEDS;
        }
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(drop));
    }

    @Override public String getDescription() { return "Dirt yields biome-specific loot."; }
    @Override public String getDescription(int level) {
        return "§7Dirt: §a" + (int) CHANCE[level - 1] + "%§7 biome-themed drop."; }
}

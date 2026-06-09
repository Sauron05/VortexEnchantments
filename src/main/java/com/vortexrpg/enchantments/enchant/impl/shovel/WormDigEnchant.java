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

/** Worm Dig: Mining dirt has chance to drop spider eye (bait). */
public class WormDigEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 12};

    public WormDigEnchant() { super("worm_dig", "Worm", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.DIRT && mat != Material.GRASS_BLOCK && mat != Material.COARSE_DIRT) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.SPIDER_EYE));
    }

    @Override public String getDescription() { return "Digging dirt may unearth worms."; }
    @Override public String getDescription(int level) {
        return "§7Dirt: §a" + (int) CHANCE[level - 1] + "%§7 to drop §5spider eye§7."; }
}

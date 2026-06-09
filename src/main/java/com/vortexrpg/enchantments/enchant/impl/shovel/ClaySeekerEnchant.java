package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Clay Seeker: Dirt near water has chance to drop clay balls. */
public class ClaySeekerEnchant extends VortexEnchant {
    private static final double[] CHANCE = {3, 5, 8};

    public ClaySeekerEnchant() { super("clay_seeker", "Clay Seeker", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.DIRT && mat != Material.GRASS_BLOCK) return;
        if (!hasWaterNearby(event.getBlock())) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.CLAY_BALL));
    }

    private boolean hasWaterNearby(Block block) {
        for (int x = -2; x <= 2; x++)
            for (int y = -2; y <= 2; y++)
                for (int z = -2; z <= 2; z++)
                    if (block.getRelative(x, y, z).getType() == Material.WATER) return true;
        return false;
    }

    @Override public String getDescription() { return "Dirt near water may drop clay."; }
    @Override public String getDescription(int level) {
        return "§7Dirt near water: §a" + (int) CHANCE[level - 1] + "%§7 to drop §7clay balls."; }
}

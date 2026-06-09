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

/** Compost: Grass/dirt mining has 3/5/8% chance to drop bone meal. */
public class CompostEnchant extends VortexEnchant {
    private static final double[] CHANCE = {3, 5, 8};

    public CompostEnchant() { super("compost", "Compost", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.GRASS_BLOCK && mat != Material.DIRT) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.BONE_MEAL));
    }

    @Override public String getDescription() { return "Digging grass/dirt may yield bone meal."; }
    @Override public String getDescription(int level) {
        return "§7Grass/dirt: §a" + (int)CHANCE[level-1] + "%§7 to drop §2bone meal§7."; }
}

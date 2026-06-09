package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Compost Hoe: Harvesting crops has a chance to drop bone meal. */
public class CompostHoeEnchant extends VortexEnchant {

    public CompostHoeEnchant() { super("compost_hoe", "Compost Hoe", EnchantRarity.COMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.WHEAT && mat != Material.CARROTS && mat != Material.POTATOES
                && mat != Material.BEETROOTS && mat != Material.MELON && mat != Material.PUMPKIN) return;
        double chance = cfg("chance", 10.0 + level * 5);
        if (!MathUtil.chance(chance)) return;
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.BONE_MEAL, level));
    }

    @Override public String getDescription() { return "Harvesting crops may drop bone meal."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: §a" + (int)(10 + level * 5) + "%§7 for §e" + level + "§7 bone meal."; }
}

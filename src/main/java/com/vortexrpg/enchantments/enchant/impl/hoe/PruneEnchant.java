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

/** Prune: Breaking leaves drops apples/sticks at higher rate. */
public class PruneEnchant extends VortexEnchant {

    public PruneEnchant() { super("prune", "Prune", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (!mat.name().endsWith("_LEAVES")) return;
        var loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        double appleChance = cfg("apple-chance", 8.0 + level * 4);
        double stickChance = cfg("stick-chance", 15.0 + level * 5);
        if (MathUtil.chance(appleChance)) {
            event.getBlock().getWorld().dropItemNaturally(loc, new ItemStack(Material.APPLE, 1));
        }
        if (MathUtil.chance(stickChance)) {
            event.getBlock().getWorld().dropItemNaturally(loc, new ItemStack(Material.STICK, level));
        }
    }

    @Override public String getDescription() { return "Breaking leaves drops more apples and sticks."; }
    @Override public String getDescription(int level) {
        return "§7Leaves: §c" + (int)(8 + level * 4) + "%§7 apple + §e" + (int)(15 + level * 5) + "%§7 sticks."; }
}

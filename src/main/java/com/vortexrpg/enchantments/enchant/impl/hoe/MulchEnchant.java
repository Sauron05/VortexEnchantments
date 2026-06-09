package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Mulch: 20/25/30% chance to drop mulch (coal) on harvest for farmland trampling resistance marker. */
public class MulchEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.20, 0.25, 0.30};

    public MulchEnchant() { super("mulch", "Mulch", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onHarvest(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            player.getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.COAL));
        }
    }

    @Override public String getDescription() { return "Chance to drop mulch when harvesting crops."; }
    @Override public String getDescription(int level) {
        return "§7" + (int)(CHANCE[level-1]*100) + "§a%§7 chance to drop mulch on harvest."; }
}

package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Thresh: Wheat harvest has 20/25/30% chance to also drop 1 string. */
public class ThreshEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.20, 0.25, 0.30};

    public ThreshEnchant() { super("thresh", "Thresh", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onHarvest(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block b = event.getBlock();
        if (b.getType() != Material.WHEAT) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            player.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.STRING));
        }
    }

    @Override public String getDescription() { return "Wheat harvest may also yield string."; }
    @Override public String getDescription(int level) {
        return "§7" + (int)(CHANCE[level-1]*100) + "§a%§7 chance to drop §astring§7 on wheat harvest."; }
}

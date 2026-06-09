package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Harvest King: All crop drops multiplied while held. */
public class HarvestKingEnchant extends VortexEnchant {

    public HarvestKingEnchant() { super("harvest_king", "Harvest King", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getBlock().getBlockData() instanceof Ageable age)) return;
        if (age.getAge() < age.getMaximumAge()) return;
        int multiplier = cfgi("multiplier", 1 + level);
        var loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        var drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand());
        for (int i = 0; i < multiplier - 1; i++) {
            for (var drop : drops) {
                event.getBlock().getWorld().dropItemNaturally(loc, drop.clone());
            }
        }
    }

    @Override public String getDescription() { return "Crop drops multiplied."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: §6" + (1 + level) + "x§7 crop drops."; }
}

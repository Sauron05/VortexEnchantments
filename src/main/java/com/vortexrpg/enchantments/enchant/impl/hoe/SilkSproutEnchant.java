package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Silk Sprout: Harvest mature crops as the full crop block (silk touch for crops). */
public class SilkSproutEnchant extends VortexEnchant {

    public SilkSproutEnchant() { super("silk_sprout", "Silk Sprout", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getBlock().getBlockData() instanceof Ageable age)) return;
        if (age.getAge() < age.getMaximumAge()) return;
        var loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        event.setDropItems(false);
        // Drop normal items plus extra based on level
        var drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand());
        for (var drop : drops) {
            drop.setAmount(drop.getAmount() + level);
            event.getBlock().getWorld().dropItemNaturally(loc, drop);
        }
    }

    @Override public String getDescription() { return "Mature crops drop extra items."; }
    @Override public String getDescription(int level) {
        return "§7Harvest mature crops: §a+" + level + "§7 to each drop."; }
}

package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Abundance: Harvesting mature crops gives +1/+2/+3 of each drop type. */
public class AbundanceEnchant extends VortexEnchant {
    private static final int[] BONUS = {1, 2, 3};

    public AbundanceEnchant() { super("abundance", "Abundance", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onHarvest(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int bonus = cfgi("bonus_drops", BONUS[level-1]);
        for (org.bukkit.inventory.ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            org.bukkit.inventory.ItemStack extra = drop.clone();
            extra.setAmount(bonus);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), extra);
        }
    }

    @Override public String getDescription() { return "Harvests yield extra drops."; }
    @Override public String getDescription(int level) {
        return "§7Mature crop harvest: §a+" + BONUS[level-1] + "§7 of each drop type."; }
}

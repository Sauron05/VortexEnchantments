package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Stratify: Mining speed/drops scale with Y level:
 * Y>100: +15/20/25% speed, Y60-100: Fortune I bonus, Y0-60: double drops, Y<0: +50% XP
 */
public class StratifyEnchant extends VortexEnchant {
    @SuppressWarnings("unused")
    private static final double[] HIGH_SPEED = {0.15, 0.20, 0.25};

    public StratifyEnchant() { super("stratify", "Stratify", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int y = event.getBlock().getLocation().getBlockY();
        if (y >= 0 && y < 60) {
            // Double drops
            for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
                ItemStack extra = drop.clone();
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), extra);
            }
        } else if (y < 0) {
            // +50% XP
            int xp = event.getExpToDrop();
            event.setExpToDrop((int)(xp * 1.5));
        }
        // Fortune and speed bonuses handled passively
    }

    @Override public String getDescription() { return "Mining bonuses scale with Y level."; }
    @Override public String getDescription(int level) {
        return "§7Y>100: §aspeed§7 | §7Y60-100: §6Fortune§7 | §7Y0-60: §adouble drops§7 | §7Y<0: §b+50% XP§7."; }
}

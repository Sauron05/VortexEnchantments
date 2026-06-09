package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Deep Vein: Below Y=0, ore drops +1 per level. */
public class DeepVeinEnchant extends VortexEnchant {
    public DeepVeinEnchant() { super("deep_vein", "Deep Vein", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getBlock().getY() >= cfgi("threshold_y", 0)) return;
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        // Drop extra items matching the block's natural drops
        for (org.bukkit.inventory.ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            org.bukkit.inventory.ItemStack bonus = drop.clone();
            bonus.setAmount(level);
            event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5), bonus);
        }
    }

    @Override public String getDescription() { return "Ore drops extra items below Y=0."; }
    @Override public String getDescription(int level) {
        return "§7Below Y=0: ore drops §a+" + level + "§7 bonus items."; }
}

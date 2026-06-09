package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Upstream: Underwater digs: items go directly to inventory. */
public class UpstreamEnchant extends VortexEnchant {
    public UpstreamEnchant() { super("upstream", "Upstream", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isUnderWater()) return;
        event.setDropItems(false);
        for (org.bukkit.inventory.ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            player.getInventory().addItem(drop);
        }
    }

    @Override public String getDescription() { return "Underwater digs go directly to your inventory."; }
    @Override public String getDescription(int level) { return "§7Underwater: drops go §bdirectly to inventory§7."; }
}

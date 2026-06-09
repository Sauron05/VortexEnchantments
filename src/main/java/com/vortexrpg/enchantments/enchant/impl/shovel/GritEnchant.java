package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Grit: Gravel always drops flint. */
public class GritEnchant extends VortexEnchant {
    public GritEnchant() { super("grit", "Grit", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getBlock().getType() != Material.GRAVEL) return;
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.FLINT));
    }

    @Override public String getDescription() { return "Gravel always drops flint."; }
    @Override public String getDescription(int level) {
        return "§7Gravel: §aguaranteed§7 flint drop."; }
}

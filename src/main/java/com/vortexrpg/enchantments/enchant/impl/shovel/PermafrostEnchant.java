package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Permafrost: Snow/ice breaks drop placeable items instead of nothing. */
public class PermafrostEnchant extends VortexEnchant {
    public PermafrostEnchant() { super("permafrost", "Permafrost", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        Material drop = switch (mat) {
            case SNOW -> Material.SNOW;
            case SNOW_BLOCK -> Material.SNOW_BLOCK;
            case ICE -> Material.ICE;
            case PACKED_ICE -> Material.PACKED_ICE;
            case BLUE_ICE -> Material.BLUE_ICE;
            default -> null;
        };
        if (drop == null) return;
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(drop));
    }

    @Override public String getDescription() { return "Snow and ice drop as placeable items."; }
    @Override public String getDescription(int level) { return "§7Snow/ice: always drops as §bplaceable block§7."; }
}

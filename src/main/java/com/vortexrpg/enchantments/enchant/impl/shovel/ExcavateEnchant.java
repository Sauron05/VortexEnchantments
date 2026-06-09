package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Excavate: Mines 3×3 area of soft blocks. */
public class ExcavateEnchant extends VortexEnchant {
    private static final Set<Material> SOFT = Set.of(
        Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL,
        Material.COARSE_DIRT, Material.PODZOL, Material.SOUL_SAND, Material.SOUL_SOIL,
        Material.CLAY, Material.SNOW_BLOCK
    );

    public ExcavateEnchant() { super("excavate", "Excavate", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SOFT.contains(event.getBlock().getType())) return;
        Block center = event.getBlock();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                Block b = center.getRelative(x, 0, z);
                if (SOFT.contains(b.getType())) {
                    b.breakNaturally(player.getInventory().getItemInMainHand());
                }
            }
        }
    }

    @Override public String getDescription() { return "Mines a 3×3 area of soft blocks."; }
    @Override public String getDescription(int level) { return "§7Mines §a3×3§7 area of dirt/sand/gravel/snow."; }
}

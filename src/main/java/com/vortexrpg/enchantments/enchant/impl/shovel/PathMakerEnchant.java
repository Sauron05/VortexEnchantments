package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Path Maker: Mining grass auto-converts adjacent grass to path blocks. */
public class PathMakerEnchant extends VortexEnchant {
    public PathMakerEnchant() { super("path_maker", "Path Maker", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getBlock().getType() != Material.GRASS_BLOCK) return;
        int radius = cfgi("radius", level);
        Block center = event.getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == 0 && z == 0) continue;
                Block b = center.getRelative(x, 0, z);
                if (b.getType() == Material.GRASS_BLOCK) {
                    b.setType(Material.DIRT_PATH);
                }
            }
        }
    }

    @Override public String getDescription() { return "Mining grass creates nearby paths."; }
    @Override public String getDescription(int level) {
        return "§7Grass: converts nearby grass to §epath blocks§7."; }
}

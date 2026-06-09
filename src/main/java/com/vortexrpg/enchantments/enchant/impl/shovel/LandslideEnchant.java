package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Landslide: Breaking dirt/sand causes 4/6/8 blocks above to collapse like gravel. */
@SuppressWarnings("deprecation")
public class LandslideEnchant extends VortexEnchant {
    private static final int[] COLLAPSE = {4, 6, 8};
    private static final Set<Material> COLLAPSIBLE = Set.of(Material.DIRT, Material.SAND, Material.GRAVEL, Material.GRASS_BLOCK);

    public LandslideEnchant() { super("landslide", "Landslide", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!COLLAPSIBLE.contains(event.getBlock().getType())) return;
        int maxCollapse = cfgi("max_collapse_" + level, COLLAPSE[level-1]);
        Block above = event.getBlock();
        for (int i = 0; i < maxCollapse; i++) {
            above = above.getRelative(BlockFace.UP);
            if (COLLAPSIBLE.contains(above.getType())) {
                above.setType(Material.AIR);
                above.getWorld().spawnFallingBlock(above.getLocation().add(0.5, 0, 0.5), Material.GRAVEL.createBlockData());
            } else break;
        }
    }

    @Override public String getDescription() { return "Breaking soft blocks causes blocks above to collapse."; }
    @Override public String getDescription(int level) {
        return "§7Digging dirt/sand: §e" + COLLAPSE[level-1] + " blocks§7 above collapse like gravel."; }
}

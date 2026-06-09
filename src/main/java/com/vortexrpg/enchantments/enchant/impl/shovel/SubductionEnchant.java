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

/** Subduction: Mining pulls blocks below to also break. */
public class SubductionEnchant extends VortexEnchant {
    private static final int[] DEPTH = {1, 2, 3};
    private static final Set<Material> SOFT = Set.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL,
            Material.RED_SAND, Material.COARSE_DIRT, Material.CLAY, Material.SOUL_SAND);

    public SubductionEnchant() { super("subduction", "Subduction", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SOFT.contains(event.getBlock().getType())) return;
        int depth = cfgi("depth", DEPTH[level - 1]);
        Block base = event.getBlock();
        for (int i = 1; i <= depth; i++) {
            Block below = base.getRelative(0, -i, 0);
            if (SOFT.contains(below.getType())) {
                below.breakNaturally(player.getInventory().getItemInMainHand());
            } else {
                break;
            }
        }
    }

    @Override public String getDescription() { return "Mining breaks blocks below too."; }
    @Override public String getDescription(int level) {
        return "§7Dig: also breaks §a" + DEPTH[level - 1] + " blocks§7 below."; }
}

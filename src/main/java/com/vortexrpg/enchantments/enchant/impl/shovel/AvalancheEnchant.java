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

/** Avalanche: Breaking soft blocks above ground causes cascade above. */
public class AvalancheEnchant extends VortexEnchant {
    private static final int[] MAX = {3, 5, 8};
    private static final Set<Material> SOFT = Set.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL,
            Material.RED_SAND, Material.COARSE_DIRT);

    public AvalancheEnchant() { super("avalanche", "Avalanche", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SOFT.contains(event.getBlock().getType())) return;
        int max = cfgi("max_cascade", MAX[level - 1]);
        Block above = event.getBlock();
        for (int i = 0; i < max; i++) {
            above = above.getRelative(0, 1, 0);
            if (SOFT.contains(above.getType())) {
                above.breakNaturally(player.getInventory().getItemInMainHand());
            } else {
                break;
            }
        }
    }

    @Override public String getDescription() { return "Breaking soft blocks cascades upward."; }
    @Override public String getDescription(int level) {
        return "§7Soft blocks: cascade §a" + MAX[level - 1] + " blocks§7 upward."; }
}

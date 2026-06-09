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

/** Trench: Mines 1×3 column of soft blocks downward. */
public class TrenchEnchant extends VortexEnchant {
    private static final Set<Material> SOFT = Set.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL,
            Material.COARSE_DIRT, Material.CLAY, Material.SOUL_SAND, Material.SOUL_SOIL);

    public TrenchEnchant() { super("trench", "Trench", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SOFT.contains(event.getBlock().getType())) return;
        Block base = event.getBlock();
        for (int i = 1; i <= level; i++) {
            Block below = base.getRelative(0, -i, 0);
            if (SOFT.contains(below.getType())) {
                below.breakNaturally(player.getInventory().getItemInMainHand());
            }
        }
    }

    @Override public String getDescription() { return "Mines soft block column downward."; }
    @Override public String getDescription(int level) {
        return "§7Mine §a" + (1 + level) + " deep§7 column of soft blocks."; }
}

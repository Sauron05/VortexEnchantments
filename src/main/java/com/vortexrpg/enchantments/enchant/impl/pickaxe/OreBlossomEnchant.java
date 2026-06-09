package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Ore Blossom: Mining ore may convert adjacent stone to same ore. */
public class OreBlossomEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 12};
    private static final Set<Material> STONE = Set.of(Material.STONE, Material.DEEPSLATE);

    public OreBlossomEnchant() { super("ore_blossom", "Ore Blossom", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (!mat.name().endsWith("_ORE")) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        Block[] adjacent = {
                event.getBlock().getRelative(1, 0, 0), event.getBlock().getRelative(-1, 0, 0),
                event.getBlock().getRelative(0, 1, 0), event.getBlock().getRelative(0, -1, 0),
                event.getBlock().getRelative(0, 0, 1), event.getBlock().getRelative(0, 0, -1)
        };
        for (Block b : adjacent) {
            if (STONE.contains(b.getType())) {
                b.setType(mat);
                break; // only convert one
            }
        }
    }

    @Override public String getDescription() { return "Mining ore may spread it to adjacent stone."; }
    @Override public String getDescription(int level) {
        return "§7Ore: §a" + (int) CHANCE[level - 1] + "%§7 to convert adjacent stone to same ore."; }
}

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
import java.util.Random;

/** Avarice: Mining ore has 3/5/8% chance to transmute adjacent stone into random ore. */
public class AvariceEnchant extends VortexEnchant {
    private static final double[] CHANCE = {3, 5, 8};
    private static final Material[] ORE_TYPES = {
        Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE,
        Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE
    };

    public AvariceEnchant() { super("avarice", "Avarice", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!isOre(event.getBlock().getType())) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        Block[] neighbors = getAdjacentBlocks(event.getBlock());
        List<Block> stones = new java.util.ArrayList<>();
        for (Block b : neighbors) {
            if (b.getType() == Material.STONE || b.getType() == Material.DEEPSLATE) stones.add(b);
        }
        if (stones.isEmpty()) return;
        Block target = stones.get(new Random().nextInt(stones.size()));
        target.setType(ORE_TYPES[new Random().nextInt(ORE_TYPES.length)]);
    }

    private Block[] getAdjacentBlocks(Block b) {
        return new Block[]{
            b.getRelative(1, 0, 0), b.getRelative(-1, 0, 0),
            b.getRelative(0, 1, 0), b.getRelative(0, -1, 0),
            b.getRelative(0, 0, 1), b.getRelative(0, 0, -1)
        };
    }

    private boolean isOre(Material m) {
        return m.name().endsWith("_ORE");
    }

    @Override public String getDescription() { return "Ore mining may convert nearby stone to ore."; }
    @Override public String getDescription(int level) {
        return "§7Mining ore: §a" + (int)CHANCE[level-1] + "%§7 chance to transmute adjacent stone into random ore."; }
}

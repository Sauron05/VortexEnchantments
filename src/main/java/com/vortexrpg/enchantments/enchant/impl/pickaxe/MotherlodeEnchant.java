package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/** Motherlode: Chance for entire ore vein to drop at once. */
public class MotherlodeEnchant extends VortexEnchant {
    private static final double[] CHANCE = {2, 3, 5};

    public MotherlodeEnchant() { super("motherlode", "Motherlode", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (!mat.name().endsWith("_ORE")) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        int maxBlocks = cfgi("max_vein", 8);
        List<Block> vein = new ArrayList<>();
        Queue<Block> queue = new LinkedList<>();
        Set<Block> visited = new java.util.HashSet<>();
        queue.add(event.getBlock());
        visited.add(event.getBlock());
        while (!queue.isEmpty() && vein.size() < maxBlocks) {
            Block c = queue.poll();
            vein.add(c);
            for (int x = -1; x <= 1; x++)
                for (int y = -1; y <= 1; y++)
                    for (int z = -1; z <= 1; z++) {
                        Block a = c.getRelative(x, y, z);
                        if (a.getType() == mat && !visited.contains(a)) {
                            visited.add(a);
                            queue.add(a);
                        }
                    }
        }
        for (Block b : vein) {
            if (b.equals(event.getBlock())) continue;
            b.breakNaturally(player.getInventory().getItemInMainHand());
        }
    }

    @Override public String getDescription() { return "Chance to break entire ore vein at once."; }
    @Override public String getDescription(int level) {
        return "§7Ore: §a" + (int) CHANCE[level - 1] + "%§7 to break entire vein (max 8)."; }
}

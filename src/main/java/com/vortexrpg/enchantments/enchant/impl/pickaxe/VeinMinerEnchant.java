package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/** Vein Miner: Mines connected same-type ore blocks. */
public class VeinMinerEnchant extends VortexEnchant {
    private static final int[] MAX = {3, 5, 8};

    public VeinMinerEnchant() { super("vein_miner", "Vein Miner", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (!mat.name().endsWith("_ORE")) return;
        int max = cfgi("max_blocks", MAX[level - 1]);
        List<Block> vein = findVein(event.getBlock(), mat, max);
        for (Block b : vein) {
            if (b.equals(event.getBlock())) continue;
            b.breakNaturally(player.getInventory().getItemInMainHand());
        }
    }

    private List<Block> findVein(Block start, Material mat, int max) {
        List<Block> found = new ArrayList<>();
        Queue<Block> queue = new LinkedList<>();
        Set<Block> visited = new java.util.HashSet<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty() && found.size() < max) {
            Block current = queue.poll();
            found.add(current);
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        Block adj = current.getRelative(x, y, z);
                        if (adj.getType() == mat && !visited.contains(adj)) {
                            visited.add(adj);
                            queue.add(adj);
                        }
                    }
                }
            }
        }
        return found;
    }

    @Override public String getDescription() { return "Mines connected ore veins."; }
    @Override public String getDescription(int level) {
        return "§7Mine connected ore: up to §a" + MAX[level - 1] + " blocks§7."; }
}

package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

/** Catalyst: Mining redstone triggers chain-mine of all adjacent redstone ore simultaneously. */
public class CatalystPickEnchant extends VortexEnchant {
    public CatalystPickEnchant() { super("catalyst_pick", "Catalyst", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.REDSTONE_ORE && mat != Material.DEEPSLATE_REDSTONE_ORE) return;
        int chainRadius = cfgi("chain_radius", 1 + level);
        int maxChain = cfgi("max_chain", 5 + level * 3);
        List<Block> toBreak = new ArrayList<>();
        findChain(event.getBlock(), mat, chainRadius, maxChain, toBreak);
        for (Block b : toBreak) {
            if (b.equals(event.getBlock())) continue;
            b.breakNaturally(player.getInventory().getItemInMainHand());
        }
    }

    private void findChain(Block center, Material mat, int radius, int maxChain, List<Block> found) {
        if (found.size() >= maxChain) return;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = center.getRelative(x, y, z);
                    if (b.getType() == mat && !found.contains(b)) {
                        found.add(b);
                        if (found.size() >= maxChain) return;
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Mining redstone chain-mines adjacent redstone ore."; }
    @Override public String getDescription(int level) {
        return "§7Mining redstone: chain-breaks all adjacent §credstone ore§7."; }
}

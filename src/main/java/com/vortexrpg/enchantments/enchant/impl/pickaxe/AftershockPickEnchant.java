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

/** Aftershock Pick: Chance to break blocks in 2-block radius. */
public class AftershockPickEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 12};

    public AftershockPickEnchant() { super("aftershock_pick", "Aftershock", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        Material mat = event.getBlock().getType();
        int radius = cfgi("radius", 2);
        Block center = event.getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Block b = center.getRelative(x, y, z);
                    if (b.getType() == mat) {
                        b.breakNaturally(player.getInventory().getItemInMainHand());
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Chance to break matching blocks in radius."; }
    @Override public String getDescription(int level) {
        return "§7Mining: §a" + (int) CHANCE[level - 1] + "%§7 to break same blocks in 2-block radius."; }
}

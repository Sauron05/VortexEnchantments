package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Sinkhole: 8/10/12% chance to remove 2 blocks below mined block. */
public class SinkholeEnchant extends VortexEnchant {
    private static final double[] CHANCE = {8, 10, 12};

    public SinkholeEnchant() { super("sinkhole", "Sinkhole", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        Block below = event.getBlock();
        int depth = cfgi("depth", 2);
        for (int i = 0; i < depth; i++) {
            below = below.getRelative(BlockFace.DOWN);
            if (!below.getType().isSolid()) break;
            below.breakNaturally(player.getInventory().getItemInMainHand());
        }
    }

    @Override public String getDescription() { return "Digging may open a sinkhole below."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)CHANCE[level-1] + "%§7 chance to remove §e2 blocks below§7 mined block."; }
}

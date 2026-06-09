package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Mass Excavate Pick: Mine 5×5 area of stone/ore blocks. */
public class MassExcavatePickEnchant extends VortexEnchant {
    public MassExcavatePickEnchant() { super("mass_excavate_pick", "Mass Excavate", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int radius = cfgi("radius", 2);
        Block center = event.getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == 0 && z == 0) continue;
                Block b = center.getRelative(x, 0, z);
                Material m = b.getType();
                if (m == Material.BEDROCK || m.isAir()) continue;
                if (m.name().endsWith("_ORE") || m == Material.STONE || m == Material.DEEPSLATE
                        || m == Material.COBBLESTONE || m == Material.NETHERRACK || m == Material.BASALT) {
                    b.breakNaturally(player.getInventory().getItemInMainHand());
                }
            }
        }
    }

    @Override public String getDescription() { return "Mine 5×5 area of stone and ore."; }
    @Override public String getDescription(int level) {
        return "§7Mine §a5×5§7 area of stone/ore blocks."; }
}

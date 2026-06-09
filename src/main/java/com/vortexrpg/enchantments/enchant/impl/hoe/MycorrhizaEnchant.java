package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

import java.util.List;

/** Mycorrhiza: Crops adjacent to log/wood blocks grow faster (passive tick). */
public class MycorrhizaEnchant extends VortexEnchant {

    public MycorrhizaEnchant() { super("mycorrhiza", "Mycorrhiza", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 80 != 0) return;
        int radius = cfgi("radius", 4 + level * 2);
        Block center = player.getLocation().getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = center.getRelative(x, 0, z);
                if (!(b.getBlockData() instanceof Ageable age)) continue;
                if (age.getAge() >= age.getMaximumAge()) continue;
                // Check if near a log/tree
                boolean nearTree = false;
                for (int dx = -2; dx <= 2 && !nearTree; dx++) {
                    for (int dz = -2; dz <= 2 && !nearTree; dz++) {
                        for (int dy = -1; dy <= 3 && !nearTree; dy++) {
                            Material m = b.getRelative(dx, dy, dz).getType();
                            if (m.name().endsWith("_LOG") || m.name().endsWith("_WOOD")) {
                                nearTree = true;
                            }
                        }
                    }
                }
                if (nearTree) {
                    b.applyBoneMeal(BlockFace.UP);
                }
            }
        }
    }

    @Override public String getDescription() { return "Crops near trees grow faster."; }
    @Override public String getDescription(int level) {
        return "§7Crops near trees within §e" + (4 + level * 2) + "§7b: grow faster."; }
}

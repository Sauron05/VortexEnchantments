package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

/** Evergreen: Crops in radius never wilt and saplings grow faster (passive). */
public class EvergreenEnchant extends VortexEnchant {

    public EvergreenEnchant() { super("evergreen", "Evergreen", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 60 != 0) return;
        int radius = cfgi("radius", 4 + level * 2);
        Block center = player.getLocation().getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = center.getRelative(x, 0, z);
                Material mat = b.getType();
                // Keep farmland moist
                if (mat == Material.FARMLAND) {
                    if (b.getBlockData() instanceof org.bukkit.block.data.type.Farmland farm) {
                        farm.setMoisture(farm.getMaximumMoisture());
                        b.setBlockData(farm);
                    }
                }
                // Boost saplings
                if (mat.name().endsWith("_SAPLING")) {
                    b.applyBoneMeal(BlockFace.UP);
                }
            }
        }
    }

    @Override public String getDescription() { return "Crops stay moist, saplings grow faster."; }
    @Override public String getDescription(int level) {
        return "§7Passive: farmland stays wet, saplings grow in §e" + (4 + level * 2) + "§7b."; }
}

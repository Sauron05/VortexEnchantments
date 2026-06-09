package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

/** Famine's Bane: Crops within radius don't need light or water to grow (passive). */
public class FaminesBaneEnchant extends VortexEnchant {

    public FaminesBaneEnchant() { super("famines_bane", "Famine's Bane", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 60 != 0) return;
        int radius = cfgi("radius", 5 + level * 3);
        Block center = player.getLocation().getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = center.getRelative(x, -1, z);
                if (b.getType() == Material.FARMLAND) {
                    if (b.getBlockData() instanceof org.bukkit.block.data.type.Farmland farm) {
                        farm.setMoisture(farm.getMaximumMoisture());
                        b.setBlockData(farm);
                    }
                }
                // Apply bone meal to crops above
                Block crop = b.getRelative(0, 1, 0);
                if (crop.getBlockData() instanceof org.bukkit.block.data.Ageable) {
                    crop.applyBoneMeal(org.bukkit.block.BlockFace.UP);
                }
            }
        }
    }

    @Override public String getDescription() { return "Crops in radius always grow and stay watered."; }
    @Override public String getDescription(int level) {
        return "§7Passive: crops in §e" + (5 + level * 3) + "§7b always grow + stay hydrated."; }
}

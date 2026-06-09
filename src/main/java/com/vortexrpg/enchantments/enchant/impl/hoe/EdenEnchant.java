package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

/** Eden: Auto-tills, waters, and plants crops around you passively. */
public class EdenEnchant extends VortexEnchant {

    private static final Material[] CROPS = {Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS};

    public EdenEnchant() { super("eden", "Eden", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 40 != 0) return;
        int radius = cfgi("radius", 1 + level);
        Block center = player.getLocation().getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block ground = center.getRelative(x, -1, z);
                Block top = center.getRelative(x, 0, z);
                // Auto-till
                if ((ground.getType() == Material.DIRT || ground.getType() == Material.GRASS_BLOCK)
                        && top.getType().isAir()) {
                    ground.setType(Material.FARMLAND);
                    // Auto-plant random crop
                    Material crop = CROPS[(int)(Math.random() * CROPS.length)];
                    top.setType(crop);
                }
                // Hydrate farmland
                if (ground.getType() == Material.FARMLAND) {
                    if (ground.getBlockData() instanceof org.bukkit.block.data.type.Farmland farm) {
                        farm.setMoisture(farm.getMaximumMoisture());
                        ground.setBlockData(farm);
                    }
                    // Boost growth
                    if (top.getBlockData() instanceof org.bukkit.block.data.Ageable) {
                        top.applyBoneMeal(BlockFace.UP);
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Auto-tills, plants, and waters around you."; }
    @Override public String getDescription(int level) {
        return "§7Passive: auto-farm in §e" + (1 + level) + "§7b radius."; }
}

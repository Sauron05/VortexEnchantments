package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Crop Rotation: After harvesting, auto-plants a different random crop. */
public class CropRotationEnchant extends VortexEnchant {

    private static final Material[] CROPS = {Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS};

    public CropRotationEnchant() { super("crop_rotation", "Crop Rotation", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block block = event.getBlock();
        if (!(block.getBlockData() instanceof Ageable age)) return;
        if (age.getAge() < age.getMaximumAge()) return;
        Material original = block.getType();
        Material newCrop;
        do {
            newCrop = CROPS[(int) (Math.random() * CROPS.length)];
        } while (newCrop == original);
        Material finalCrop = newCrop;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Block replant = block.getLocation().getBlock();
            if (replant.getType() == Material.AIR) {
                replant.setType(finalCrop);
            }
        }, 1L);
    }

    @Override public String getDescription() { return "Harvesting auto-plants a different crop."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: auto-plants §ea random different crop§7."; }
}

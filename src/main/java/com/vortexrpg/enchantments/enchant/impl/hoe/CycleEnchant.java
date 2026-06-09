package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Map;

/** Cycle: Harvesting auto-replants using 1 dropped seed. */
public class CycleEnchant extends VortexEnchant {
    private static final Map<Material, Material> CROP_SEED = Map.of(
        Material.WHEAT, Material.WHEAT_SEEDS,
        Material.CARROTS, Material.CARROT,
        Material.POTATOES, Material.POTATO,
        Material.BEETROOTS, Material.BEETROOT_SEEDS,
        Material.NETHER_WART, Material.NETHER_WART
    );

    public CycleEnchant() { super("cycle", "Cycle", EnchantRarity.COMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onHarvest(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material crop = event.getBlock().getType();
        Material seed = CROP_SEED.get(crop);
        if (seed == null) return;
        Block farmland = event.getBlock().getRelative(BlockFace.DOWN);
        Block cropBlock = event.getBlock();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (cropBlock.getType() == Material.AIR && farmland.getType() == Material.FARMLAND) {
                cropBlock.setType(crop);
                if (cropBlock.getBlockData() instanceof Ageable ageable) {
                    ageable.setAge(0);
                    cropBlock.setBlockData(ageable);
                }
                // Remove 1 seed from drops
                event.getBlock().getDrops(player.getInventory().getItemInMainHand());
            }
        }, 1L);
    }

    @Override public String getDescription() { return "Harvesting auto-replants crops."; }
    @Override public String getDescription(int level) { return "§7Harvest: §aauto-replants§7 using 1 dropped seed."; }
}

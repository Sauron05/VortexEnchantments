package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

/** Canopy: Tilling under leaves has 8/10/15% to also drop an apple. */
public class CanopyEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.08, 0.10, 0.15};
    private static final Set<Material> LEAVES = Set.of(
        Material.OAK_LEAVES, Material.BIRCH_LEAVES, Material.SPRUCE_LEAVES,
        Material.JUNGLE_LEAVES, Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES,
        Material.CHERRY_LEAVES, Material.MANGROVE_LEAVES
    );

    public CanopyEnchant() { super("canopy", "Canopy", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block b = event.getBlock();
        Block above = b.getRelative(0, 1, 0);
        if (!LEAVES.contains(above.getType())) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            player.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.APPLE));
        }
    }

    @Override public String getDescription() { return "Digging under leaves may drop an apple."; }
    @Override public String getDescription(int level) {
        return "§7" + (int)(CHANCE[level-1]*100) + "§a%§7 to drop §aapple§7 when tilling under leaves."; }
}

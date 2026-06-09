package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Dirt Shield: Mining soft blocks grants small absorption hearts. */
public class DirtShieldEnchant extends VortexEnchant {
    private static final double[] ABSORB = {1, 2, 3};
    private static final Set<Material> SOFT = Set.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL, Material.CLAY);

    public DirtShieldEnchant() { super("dirt_shield", "Dirt Shield", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!SOFT.contains(event.getBlock().getType())) return;
        double absorb = cfg("absorb", ABSORB[level - 1]);
        double current = player.getAbsorptionAmount();
        player.setAbsorptionAmount(Math.min(current + absorb, 6.0));
    }

    @Override public String getDescription() { return "Mining soft blocks grants absorption."; }
    @Override public String getDescription(int level) {
        return "§7Soft blocks: §e+" + (int) ABSORB[level - 1] + "§7 absorption hearts."; }
}

package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Peat: Swamp biome digging has 10/15/20% to drop peat (represented by coal). */
@SuppressWarnings("deprecation")
public class PeatEnchant extends VortexEnchant {
    private static final double[] CHANCE = {10, 15, 20};

    public PeatEnchant() { super("peat", "Peat", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getBlock().getBiome() != org.bukkit.block.Biome.SWAMP &&
            event.getBlock().getBiome() != org.bukkit.block.Biome.MANGROVE_SWAMP) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        // Peat = coal as fuel placeholder
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.COAL));
        player.sendActionBar("§8Peat uncovered!");
    }

    @Override public String getDescription() { return "Swamp digging may yield peat."; }
    @Override public String getDescription(int level) {
        return "§7Swamp: §a" + (int)CHANCE[level-1] + "%§7 to drop peat (rich fuel)."; }
}

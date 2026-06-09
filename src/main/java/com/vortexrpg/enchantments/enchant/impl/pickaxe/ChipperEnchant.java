package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Chipper: Mining stone has chance to also drop flint. */
public class ChipperEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 12};

    public ChipperEnchant() { super("chipper", "Chipper", EnchantRarity.COMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.STONE && mat != Material.DEEPSLATE) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.FLINT));
    }

    @Override public String getDescription() { return "Stone mining may yield flint."; }
    @Override public String getDescription(int level) {
        return "§7Stone: §a" + (int) CHANCE[level - 1] + "%§7 chance to also drop §7flint."; }
}

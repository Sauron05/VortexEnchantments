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

/** Pressure: Deepslate ore has 10/12/15% chance to upgrade drop tier. */
public class PressureEnchant extends VortexEnchant {
    private static final double[] CHANCE = {10, 12, 15};

    public PressureEnchant() { super("pressure", "Pressure", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getBlock().getType().name().startsWith("DEEPSLATE_")) return;
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        Material upgraded = getUpgrade(event.getBlock().getType());
        if (upgraded == null) return;
        event.setDropItems(false);
        event.getBlock().getLocation().getWorld().dropItemNaturally(
            event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(upgraded));
    }

    private Material getUpgrade(Material mat) {
        return switch (mat) {
            case DEEPSLATE_IRON_ORE -> Material.RAW_GOLD;
            case DEEPSLATE_GOLD_ORE -> Material.DIAMOND;
            case DEEPSLATE_DIAMOND_ORE -> Material.EMERALD;
            default -> null;
        };
    }

    @Override public String getDescription() { return "Deepslate ores may yield upgraded drops."; }
    @Override public String getDescription(int level) {
        return "§7Deepslate ore: §a" + (int)CHANCE[level-1] + "%§7 chance to upgrade drop tier."; }
}

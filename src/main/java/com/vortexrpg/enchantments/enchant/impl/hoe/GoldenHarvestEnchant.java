package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Golden Harvest: Rare chance for golden variants of crop drops. */
public class GoldenHarvestEnchant extends VortexEnchant {

    public GoldenHarvestEnchant() { super("golden_harvest", "Golden Harvest", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getBlock().getBlockData() instanceof Ageable age)) return;
        if (age.getAge() < age.getMaximumAge()) return;
        double chance = cfg("chance", 2.0 + level * 2);
        if (!MathUtil.chance(chance)) return;
        Material mat = event.getBlock().getType();
        Material golden = switch (mat) {
            case CARROTS -> Material.GOLDEN_CARROT;
            case POTATOES -> Material.GOLDEN_APPLE;
            case WHEAT -> Material.GOLDEN_APPLE;
            default -> null;
        };
        if (golden == null) return;
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(golden, 1));
    }

    @Override public String getDescription() { return "Rare chance for golden crop variants."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: §e" + (int)(2 + level * 2) + "%§7 chance for §6golden item§7."; }
}

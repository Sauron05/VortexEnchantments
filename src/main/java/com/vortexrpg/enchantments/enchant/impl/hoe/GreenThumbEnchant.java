package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Green Thumb: Harvesting crops has a chance to drop bonus seeds. */
public class GreenThumbEnchant extends VortexEnchant {

    public GreenThumbEnchant() { super("green_thumb", "Green Thumb", EnchantRarity.COMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance", 15.0 + level * 5);
        if (!MathUtil.chance(chance)) return;
        Material mat = event.getBlock().getType();
        Material seed = switch (mat) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            default -> null;
        };
        if (seed == null) return;
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(seed, level));
    }

    @Override public String getDescription() { return "Harvesting gives bonus seeds."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: §a" + (int)(15 + level * 5) + "%§7 chance for §e+" + level + "§7 bonus seeds."; }
}

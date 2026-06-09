package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Seed Saver: Harvesting always drops at least 1 extra seed. */
public class SeedSaverEnchant extends VortexEnchant {

    public SeedSaverEnchant() { super("seed_saver", "Seed Saver", EnchantRarity.COMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
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

    @Override public String getDescription() { return "Harvesting always gives extra seeds."; }
    @Override public String getDescription(int level) {
        return "§7Harvest: guaranteed §e+" + level + "§7 extra seeds."; }
}

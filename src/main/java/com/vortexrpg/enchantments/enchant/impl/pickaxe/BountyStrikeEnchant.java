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
import java.util.Random;

/** Bounty Strike: Rare chance any block drops a treasure item. */
public class BountyStrikeEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.5, 0.8, 1.2};
    private static final Material[] TREASURES = {
            Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT, Material.LAPIS_LAZULI};

    public BountyStrikeEnchant() { super("bounty_strike", "Bounty Strike", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        Material treasure = TREASURES[new Random().nextInt(TREASURES.length)];
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(treasure));
    }

    @Override public String getDescription() { return "Rare treasure drops while mining."; }
    @Override public String getDescription(int level) {
        return "§7Mining: §a" + CHANCE[level - 1] + "%§7 to drop §btreasure§7."; }
}

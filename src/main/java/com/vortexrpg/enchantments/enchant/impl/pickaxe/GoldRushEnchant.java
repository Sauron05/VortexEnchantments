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

/** Gold Rush: Mining any ore has chance to also drop gold nuggets. */
public class GoldRushEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 12};

    public GoldRushEnchant() { super("gold_rush", "Gold Rush", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        int amount = 1 + new Random().nextInt(3);
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                new ItemStack(Material.GOLD_NUGGET, amount));
    }

    @Override public String getDescription() { return "Ore may also drop gold nuggets."; }
    @Override public String getDescription(int level) {
        return "§7Ore: §a" + (int) CHANCE[level - 1] + "%§7 to also drop §6gold nuggets§7."; }
}

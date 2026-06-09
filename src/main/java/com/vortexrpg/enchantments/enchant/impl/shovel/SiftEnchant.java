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
import java.util.Random;

/** Sift: Gravel mining has 8/12/18% chance to find rare items. */
public class SiftEnchant extends VortexEnchant {
    private static final double[] CHANCE = {8, 12, 18};
    private static final Material[] LOOT = {Material.FLINT, Material.CLAY_BALL, Material.GOLD_NUGGET, Material.IRON_NUGGET};

    public SiftEnchant() { super("sift", "Sift", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getBlock().getType() != Material.GRAVEL) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level-1]))) return;
        Material find = LOOT[new Random().nextInt(LOOT.length)];
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(find));
    }

    @Override public String getDescription() { return "Gravel mining may uncover rare finds."; }
    @Override public String getDescription(int level) {
        return "§7Gravel: §a" + (int)CHANCE[level-1] + "%§7 chance to find flint/clay/nuggets."; }
}

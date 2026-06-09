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
import java.util.Set;

/** Treasure Dig: Sand/dirt has chance to yield gold nuggets. */
public class TreasureDigEnchant extends VortexEnchant {
    private static final double[] CHANCE = {1, 2, 3};
    private static final Set<Material> DIGGABLE = Set.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.RED_SAND, Material.GRAVEL);

    public TreasureDigEnchant() { super("treasure_dig", "Treasure Dig", EnchantRarity.RARE, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!DIGGABLE.contains(event.getBlock().getType())) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                new ItemStack(Material.GOLD_NUGGET, 1 + new java.util.Random().nextInt(2)));
    }

    @Override public String getDescription() { return "Digging may yield gold nuggets."; }
    @Override public String getDescription(int level) {
        return "§7Dirt/Sand: §a" + (int) CHANCE[level - 1] + "%§7 to drop §6gold nuggets§7."; }
}

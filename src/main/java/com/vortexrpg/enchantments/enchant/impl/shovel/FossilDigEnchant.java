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
import java.util.Set;

/** Fossil Dig: Chance to drop bones/bone blocks when mining dirt/sand. */
public class FossilDigEnchant extends VortexEnchant {
    private static final double[] CHANCE = {3, 5, 8};
    private static final Set<Material> DIGGABLE = Set.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL);

    public FossilDigEnchant() { super("fossil_dig", "Fossil Dig", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!DIGGABLE.contains(event.getBlock().getType())) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        Material drop = new Random().nextBoolean() ? Material.BONE : Material.BONE_BLOCK;
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(drop));
    }

    @Override public String getDescription() { return "Digging may unearth fossils."; }
    @Override public String getDescription(int level) {
        return "§7Dig: §a" + (int) CHANCE[level - 1] + "%§7 to drop §fbones§7."; }
}

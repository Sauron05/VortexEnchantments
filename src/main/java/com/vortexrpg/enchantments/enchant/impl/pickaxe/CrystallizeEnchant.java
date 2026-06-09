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
import java.util.Set;

/** Crystallize: Mining quartz/amethyst has chance to drop XP bottle. */
public class CrystallizeEnchant extends VortexEnchant {
    private static final double[] CHANCE = {10, 15, 20};
    private static final Set<Material> CRYSTALS = Set.of(
            Material.NETHER_QUARTZ_ORE, Material.AMETHYST_BLOCK, Material.BUDDING_AMETHYST);

    public CrystallizeEnchant() { super("crystallize", "Crystallize", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!CRYSTALS.contains(event.getBlock().getType())) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                new ItemStack(Material.EXPERIENCE_BOTTLE));
    }

    @Override public String getDescription() { return "Crystal blocks may drop XP bottles."; }
    @Override public String getDescription(int level) {
        return "§7Quartz/Amethyst: §a" + (int) CHANCE[level - 1] + "%§7 to drop §dXP bottle§7."; }
}

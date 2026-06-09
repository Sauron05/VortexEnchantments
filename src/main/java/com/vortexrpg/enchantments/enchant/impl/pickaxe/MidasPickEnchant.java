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

/** Midas Pick: Chance for stone drops to become gold nuggets. */
public class MidasPickEnchant extends VortexEnchant {
    private static final double[] CHANCE = {3, 5, 8};
    private static final Set<Material> STONES = Set.of(
            Material.STONE, Material.DEEPSLATE, Material.COBBLESTONE, Material.COBBLED_DEEPSLATE);

    public MidasPickEnchant() { super("midas_pick", "Midas Pick", EnchantRarity.EPIC, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!STONES.contains(event.getBlock().getType())) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                new ItemStack(Material.GOLD_NUGGET, 1 + new java.util.Random().nextInt(2)));
    }

    @Override public String getDescription() { return "Stone drops may become gold nuggets."; }
    @Override public String getDescription(int level) {
        return "§7Stone: §a" + (int) CHANCE[level - 1] + "%§7 to drop §6gold nuggets§7."; }
}

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

/** Pulverize: Stone has chance to drop as sand. */
public class PulverizeEnchant extends VortexEnchant {
    private static final double[] CHANCE = {8, 12, 18};
    private static final Set<Material> STONES = Set.of(
            Material.STONE, Material.COBBLESTONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE);

    public PulverizeEnchant() { super("pulverize", "Pulverize", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!STONES.contains(event.getBlock().getType())) return;
        if (!MathUtil.chance(cfg("chance", CHANCE[level - 1]))) return;
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.SAND));
    }

    @Override public String getDescription() { return "Stone may drop as sand."; }
    @Override public String getDescription(int level) {
        return "§7Stone: §a" + (int) CHANCE[level - 1] + "%§7 to drop as §esand§7."; }
}

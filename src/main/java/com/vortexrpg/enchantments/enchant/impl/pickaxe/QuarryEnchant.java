package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Quarry: Mines 1×3 column downward. */
public class QuarryEnchant extends VortexEnchant {
    private static final Set<Material> MINEABLE = Set.of(
            Material.STONE, Material.DEEPSLATE, Material.COBBLESTONE, Material.COBBLED_DEEPSLATE,
            Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.TUFF,
            Material.NETHERRACK, Material.BASALT, Material.BLACKSTONE);

    public QuarryEnchant() { super("quarry", "Quarry", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Block base = event.getBlock();
        for (int i = 1; i <= level; i++) {
            Block below = base.getRelative(0, -i, 0);
            if (MINEABLE.contains(below.getType()) || below.getType().name().endsWith("_ORE")) {
                below.breakNaturally(player.getInventory().getItemInMainHand());
            }
        }
    }

    @Override public String getDescription() { return "Mines column downward."; }
    @Override public String getDescription(int level) {
        return "§7Mine §a" + (1 + level) + " blocks§7 deep column."; }
}

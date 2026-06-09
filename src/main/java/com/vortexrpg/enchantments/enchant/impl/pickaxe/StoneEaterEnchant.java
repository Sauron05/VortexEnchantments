package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Set;

/** Stone Eater: Mining stone restores hunger. */
public class StoneEaterEnchant extends VortexEnchant {
    private static final int[] FOOD = {1, 2, 3};
    private static final Set<Material> STONES = Set.of(
            Material.STONE, Material.DEEPSLATE, Material.COBBLESTONE,
            Material.COBBLED_DEEPSLATE, Material.ANDESITE, Material.DIORITE, Material.GRANITE);

    public StoneEaterEnchant() { super("stone_eater", "Stone Eater", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!STONES.contains(event.getBlock().getType())) return;
        int food = cfgi("food_restore", FOOD[level - 1]);
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + food));
    }

    @Override public String getDescription() { return "Mining stone restores hunger."; }
    @Override public String getDescription(int level) {
        return "§7Stone: restores §a" + FOOD[level - 1] + "§7 hunger."; }
}

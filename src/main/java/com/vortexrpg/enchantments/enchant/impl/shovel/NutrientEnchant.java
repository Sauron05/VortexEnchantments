package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Nutrient: Mining soil blocks restores hunger and saturation. */
public class NutrientEnchant extends VortexEnchant {

    public NutrientEnchant() { super("nutrient", "Nutrient", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.DIRT && mat != Material.GRASS_BLOCK && mat != Material.FARMLAND
                && mat != Material.ROOTED_DIRT && mat != Material.MUD && mat != Material.PODZOL
                && mat != Material.MYCELIUM && mat != Material.COARSE_DIRT) return;
        int foodRestore = cfgi("food-restore", level);
        float satRestore = (float) cfg("saturation-restore", level * 0.5);
        int newFood = Math.min(20, player.getFoodLevel() + foodRestore);
        float newSat = Math.min(newFood, player.getSaturation() + satRestore);
        player.setFoodLevel(newFood);
        player.setSaturation(newSat);
    }

    @Override public String getDescription() { return "Mining soil restores hunger."; }
    @Override public String getDescription(int level) {
        return "§7Mine soil: +§a" + level + "🍗§7 + §a" + String.format("%.1f", level * 0.5) + "§7 saturation."; }
}

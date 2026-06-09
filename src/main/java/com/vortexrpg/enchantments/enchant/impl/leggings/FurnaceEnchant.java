package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Furnace: Slowly regenerate hunger over time. */
public class FurnaceEnchant extends VortexEnchant {
    public FurnaceEnchant() { super("furnace", "Furnace", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % (80 / level) != 0) return;
        if (player.getFoodLevel() < 20) {
            player.setFoodLevel(Math.min(player.getFoodLevel() + 1, 20));
        }
    }

    @Override public String getDescription() { return "Slowly restores hunger over time."; }
    @Override public String getDescription(int level) {
        return "§7Passively restores §a1§7 hunger every §a" + (80/level) + "§7 ticks."; }
}

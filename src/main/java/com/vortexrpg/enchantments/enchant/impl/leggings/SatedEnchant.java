package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Sated: Slowly restores hunger while standing still.
 */
public class SatedEnchant extends VortexEnchant {
    public SatedEnchant() { super("sated", "Sated", EnchantRarity.COMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.getFoodLevel() >= 20) return;
        // Only restore if not moving (velocity near zero)
        if (player.getVelocity().lengthSquared() > 0.01) return;
        int restore = cfgi("food_per_tick", level);
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + restore));
    }

    @Override public String getDescription(int level) {
        return "§7Slowly restore §a" + level + " §7hunger while standing still.";
    }
}

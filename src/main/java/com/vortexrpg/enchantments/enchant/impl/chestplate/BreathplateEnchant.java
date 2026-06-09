package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Breathplate: Increases maximum air supply while worn.
 */
public class BreathplateEnchant extends VortexEnchant {
    public BreathplateEnchant() { super("breathplate", "Breathplate", EnchantRarity.COMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.isInWater() && player.getRemainingAir() < player.getMaximumAir()) {
            int bonus = cfgi("air_per_tick", 2 * level);
            player.setRemainingAir(Math.min(player.getMaximumAir(), player.getRemainingAir() + bonus));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Recovers §a" + (2 * level) + " §7extra air per second underwater.";
    }
}

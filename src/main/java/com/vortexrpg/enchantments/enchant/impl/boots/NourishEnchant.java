package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Nourish: Slowly restores saturation while walking.
 */
public class NourishEnchant extends VortexEnchant {
    public NourishEnchant() { super("nourish", "Nourish", EnchantRarity.COMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.getFoodLevel() >= 20) return;
        float sat = (float)(cfgd("saturation", 0.2 * level));
        player.setSaturation(Math.min(player.getSaturation() + sat, (float) player.getFoodLevel()));
    }

    @Override public String getDescription(int level) {
        return "§7Slowly restores saturation while walking.";
    }
}

package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * LightStep: Passive Speed I while wearing boots.
 */
public class LightStepEnchant extends VortexEnchant {
    public LightStepEnchant() { super("light_step", "Light Step", EnchantRarity.COMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, level - 1, true, false, true));
    }

    @Override public String getDescription(int level) {
        return "§7Grants passive §bSpeed " + level + "§7.";
    }
}

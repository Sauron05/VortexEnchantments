package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Godlike: Passively grants Resistance and Strength at low amplifier.
 */
public class GodlikeEnchant extends VortexEnchant {
    public GodlikeEnchant() { super("godlike", "Godlike", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        int amp = level - 1;
        if (!player.hasPotionEffect(PotionEffectType.RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, amp, true, false, false));
        }
        if (!player.hasPotionEffect(PotionEffectType.STRENGTH)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, amp, true, false, false));
        }
        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, amp, true, false, false));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Passively gain §bResistance§7, §cStrength§7, and §dRegeneration " + level + "§7.";
    }
}

package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Thermal (leggings): Fire Resistance passive. */
public class ThermalLeggingsEnchant extends VortexEnchant {
    public ThermalLeggingsEnchant() { super("thermal_leggings", "Thermal", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300, 0, true, false, false));
        }
    }

    @Override public String getDescription() { return "Grants permanent Fire Resistance."; }
    @Override public String getDescription(int level) { return "§7Passive §aFire Resistance§7 while worn."; }
}

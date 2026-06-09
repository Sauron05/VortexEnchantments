package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** ThermalStep: Immunity to magma block damage while worn; Fire Resistance passively. */
public class ThermalStepEnchant extends VortexEnchant {
    public ThermalStepEnchant() { super("thermal_step", "Thermal Step", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300, 0, true, false, false));
        }
    }

    @Override public String getDescription() { return "Magma and lava don't burn you."; }
    @Override public String getDescription(int level) { return "§7Passive §aFire Resistance§7 while worn."; }
}

package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Dowse: Grants Water Breathing passively while worn. */
public class DowseEnchant extends VortexEnchant {
    public DowseEnchant() { super("dowse", "Dowse", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 300, 0, true, false, false));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Passive §aWater Breathing§7 while worn.";
    }
}

package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Ventilate: Passively removes Mining Fatigue whenever detected. */
public class VentilateEnchant extends VortexEnchant {
    public VentilateEnchant() { super("ventilate", "Ventilate", EnchantRarity.COMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.hasPotionEffect(PotionEffectType.MINING_FATIGUE)) {
            PotionEffect eff = player.getPotionEffect(PotionEffectType.MINING_FATIGUE);
            if (eff != null && eff.getAmplifier() < level) {
                player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
            }
        }
    }

    @Override public String getDescription(int level) {
        return "§7Removes §cMining Fatigue §7up to level §a" + level + "§7.";
    }
}

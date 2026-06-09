package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Perceive: Grants Conduit Power while in water (underwater mining/vision).
 */
public class PerceiveEnchant extends VortexEnchant {
    public PerceiveEnchant() { super("perceive", "Perceive", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isInWater()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 60, level - 1, true, false, false));
    }

    @Override public String getDescription(int level) {
        return "§7Grants §aConduit Power " + level + "§7 while in water.";
    }
}

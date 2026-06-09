package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Gait: Detect block under foot; grant Dolphin's Grace while in water, Speed on land. */
public class GaitEnchant extends VortexEnchant {
    public GaitEnchant() { super("gait", "Gait", EnchantRarity.UNCOMMON, 2, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.isInWater()) {
            if (!player.hasPotionEffect(org.bukkit.potion.PotionEffectType.DOLPHINS_GRACE)) {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DOLPHINS_GRACE, 100, 0, true, false, false));
            }
        } else {
            if (!player.hasPotionEffect(org.bukkit.potion.PotionEffectType.SPEED)) {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 100, level - 1, true, false, false));
            }
        }
    }

    @Override public String getDescription() { return "Swim faster in water; sprint faster on land."; }
    @Override public String getDescription(int level) {
        return "§7Water: §aDolphin's Grace§7. Land: §aSpeed " + level + "§7."; }
}

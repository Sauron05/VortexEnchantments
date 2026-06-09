package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Mantle: 25/30/35% chance that damage triggers Absorption I for 5s instead of normal damage application. */
public class MantleEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.25, 0.30, 0.35};

    public MantleEnchant() { super("mantle", "Mantle", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.ABSORPTION, 100, 0));
        }
    }

    @Override public String getDescription() { return "Hits sometimes grant brief Absorption."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level-1]*100) + "§a%§7 chance to gain §aAbsorption I§7 for 5s on hit."; }
}

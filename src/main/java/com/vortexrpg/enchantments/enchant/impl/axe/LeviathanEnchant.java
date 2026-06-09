package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Leviathan: Deals massive bonus damage to targets in or near water.
 * +50/75/100% damage when target is in water. +25/37/50% when raining.
 */
public class LeviathanEnchant extends VortexEnchant {

    public LeviathanEnchant() {
        super("leviathan", "Leviathan", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        boolean inWater = victim.isInWater();
        boolean raining = victim.getWorld().hasStorm();

        if (!inWater && !raining) return;

        double bonus = 0;
        if (inWater) {
            bonus += cfgd("water_bonus", 0.25 + level * 0.25);
        }
        if (raining) {
            bonus += cfgd("rain_bonus", 0.125 + level * 0.125);
        }

        event.setDamage(event.getDamage() * (1 + bonus));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.DRIPPING_WATER, 20, 0.5);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.BUBBLE_POP, 10, 0.4);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 0.6f, 0.8f);
    }

    @Override
    public String getDescription(int level) {
        int water = (int) ((0.25 + level * 0.25) * 100);
        int rain = (int) ((0.125 + level * 0.125) * 100);
        return "§7+" + water + "% damage in water, +" + rain + "% in rain.";
    }
}

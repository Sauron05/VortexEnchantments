package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * ThunderClap: Critical hit creates an AoE Slowness IV blast in 4/5/6 block radius for 2 seconds.
 * Like clapping thunder through the ground.
 */
public class ThunderClapEnchant extends VortexEnchant {

    public ThunderClapEnchant() {
        super("thunderclap", "Thunder Clap", EnchantRarity.EPIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        // Critical hit check: falling + not on ground
        if (attacker.getFallDistance() <= 0) return;

        double radius = cfgd("radius", 3.0 + level);
        int duration = cfgi("slow_duration", 40);

        for (LivingEntity nearby : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (nearby.equals(attacker)) continue;
            nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 3, false, true));
        }

        ParticleUtil.drawCircle(victim.getLocation(), radius, 24, Particle.ELECTRIC_SPARK);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.6f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        int r = 3 + level;
        return "§7Crit: §eSlowness IV §7AoE in §e" + r + "-block §7radius for §e2s§7.";
    }
}

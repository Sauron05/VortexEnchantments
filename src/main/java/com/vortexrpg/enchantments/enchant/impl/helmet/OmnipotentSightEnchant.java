package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Omnipotent Sight: When hit, freeze time for all enemies within X blocks for 2-4s.
 * Applies Slowness 127 + Blindness. 60s CD.
 */
public class OmnipotentSightEnchant extends VortexEnchant {
    public OmnipotentSightEnchant() { super("omnipotent_sight", "Omnipotent Sight", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;

        double radius = cfgd("radius", 6.0 + level * 2.0);
        int dur = cfgi("duration", 40 + level * 20);

        for (LivingEntity e : com.vortexrpg.enchantments.util.MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (e.equals(victim)) continue;
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, dur, 127, false, false, false));
            e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, dur, 0, false, false, false));
            ParticleUtil.spawn(e.getLocation().add(0, 1, 0), Particle.SCULK_SOUL, 10, 0.5);
        }

        ParticleUtil.burst(victim.getLocation(), Particle.SCULK_SOUL, 30, 2.0);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_WARDEN_ROAR, 0.8f, 0.5f);
        setCooldownFromConfig(victim, "cooldown", 60.0);
    }

    @Override public String getDescription(int level) {
        return "§7On hit: §5§lFREEZE TIME §7for all enemies in " + (int)(6 + level * 2) + " blocks. §860s CD.";
    }
}

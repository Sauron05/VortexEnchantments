package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Apocalypse: Mega-bolt that creates a 5-block AoE cataclysm on impact.
 * All negative effects applied, massive damage, screen shake.
 * Ultimate crossbow enchant. 60s cooldown.
 */
public class ApocalypseEnchant extends VortexEnchant {

    public ApocalypseEnchant() {
        super("apocalypse", "Apocalypse", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        double radius = cfgd("radius", 3.0 + level);
        double aoeDamage = cfgd("aoe_damage", 4.0 + level * 2.0);
        int effectDuration = cfgi("effect_duration", 2 + level) * 20;
        int effectAmplifier = cfgi("effect_amplifier", level - 1);

        Location center = victim.getLocation();

        // Apply all negative effects to nearby entities
        for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
            if (entity.equals(shooter)) continue;

            entity.damage(aoeDamage, shooter);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, effectDuration, effectAmplifier, false, true));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, effectDuration, effectAmplifier, false, true));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, effectDuration, effectAmplifier, false, true));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, effectDuration, effectAmplifier, false, true));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectDuration / 2, 0, false, true));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, effectDuration, effectAmplifier + 1, false, true));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, effectDuration, effectAmplifier, false, true));
        }

        // Massive visual/audio
        event.setDamage(event.getDamage() * cfgd("damage_multiplier", 1.5 + level * 0.25));
        ParticleUtil.burst(center, Particle.EXPLOSION, 5, 2.0);
        ParticleUtil.drawCircle(center, radius, 30, Particle.SOUL_FIRE_FLAME);
        ParticleUtil.spawnHelix(center, Particle.FLAME, 10, 5.0);

        SoundUtil.play(center, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.3f);
        SoundUtil.play(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
        SoundUtil.play(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 0.5f);

        setCooldownFromConfig(shooter, "cooldown", 60.0);
    }

    @Override
    public String getDescription(int level) {
        double r = 3 + level;
        return "§7Bolt: §4§l☠ APOCALYPSE §7— §e" + r + "-block §7AoE, ALL debuffs, massive damage. 60s CD.";
    }
}

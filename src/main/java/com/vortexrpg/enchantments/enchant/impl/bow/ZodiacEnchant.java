package com.vortexrpg.enchantments.enchant.impl.bow;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Zodiac: Arrow effect changes based on the time of day (12 zodiac signs).
 * Each 1000-tick window grants a different on-hit effect.
 */
public class ZodiacEnchant extends VortexEnchant {

    public ZodiacEnchant() {
        super("zodiac", "Zodiac", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        long time = shooter.getWorld().getTime();
        int sign = (int) (time / 2000) % 12;
        double mult = cfgd("base_multiplier", 0.8 + level * 0.1);

        switch (sign) {
            case 0 -> { // Aries — raw power
                event.setDamage(event.getDamage() * (1.0 + mult));
                ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.FLAME, 10, 0.3);
            }
            case 1 -> { // Taurus — defense break
                event.setDamage(event.getDamage() + 4.0 * mult);
                ParticleUtil.spawn(victim.getLocation(), Particle.CRIT, 10, 0.3);
            }
            case 2 -> { // Gemini — double hit
                victim.damage(event.getDamage() * 0.5 * mult, shooter);
                ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 10, 0.4);
            }
            case 3 -> { // Cancer — lifesteal
                double heal = event.getDamage() * 0.3 * mult;
                double maxHp = shooter.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                shooter.setHealth(Math.min(shooter.getHealth() + heal, maxHp));
                ParticleUtil.spawn(shooter.getLocation(), Particle.HEART, 4, 0.3);
            }
            case 4 -> { // Leo — fire
                victim.setFireTicks((int) (60 * mult));
                ParticleUtil.spawn(victim.getLocation(), Particle.FLAME, 12, 0.4);
            }
            case 5 -> { // Virgo — cleanse shooter
                shooter.getActivePotionEffects().stream()
                        .filter(e -> !e.getType().equals(PotionEffectType.SPEED))
                        .forEach(e -> shooter.removePotionEffect(e.getType()));
                ParticleUtil.spawn(shooter.getLocation(), Particle.HAPPY_VILLAGER, 8, 0.5);
            }
            case 6 -> { // Libra — balance HP
                double avg = (shooter.getHealth() + victim.getHealth()) / 2.0;
                double maxShooter = shooter.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                shooter.setHealth(Math.min(avg, maxShooter));
                ParticleUtil.spawn(victim.getLocation(), Particle.END_ROD, 10, 0.4);
            }
            case 7 -> { // Scorpio — poison
                victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int) (80 * mult), 1, false, true));
                ParticleUtil.spawn(victim.getLocation(), Particle.ITEM_SLIME, 10, 0.3);
            }
            case 8 -> { // Sagittarius — speed
                shooter.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (60 * mult), 1, false, true));
                ParticleUtil.spawn(shooter.getLocation(), Particle.CLOUD, 6, 0.3);
            }
            case 9 -> { // Capricorn — slowness
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, (int) (60 * mult), 1, false, true));
                ParticleUtil.spawn(victim.getLocation(), Particle.SNOWFLAKE, 10, 0.3);
            }
            case 10 -> { // Aquarius — knockback
                victim.setVelocity(victim.getLocation().toVector()
                        .subtract(shooter.getLocation().toVector()).normalize().multiply(mult));
                ParticleUtil.spawn(victim.getLocation(), Particle.SPLASH, 10, 0.5);
            }
            case 11 -> { // Pisces — wither
                victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (int) (40 * mult), 0, false, true));
                ParticleUtil.spawn(victim.getLocation(), Particle.SMOKE, 10, 0.3);
            }
        }

        SoundUtil.play(victim.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.6f, 1.0f + sign * 0.1f);
    }

    @Override
    public String getDescription(int level) {
        return "§7Arrow effect changes with time: §e12 zodiac signs§7, each unique.";
    }
}

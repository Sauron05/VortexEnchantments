package com.vortexrpg.enchantments.enchant.impl.bow;

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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Apollyon: Kill creates a death zone — entities within 5 blocks lose 5% HP/s for 5s.
 * The angel of the abyss claims the area around each kill.
 */
public class ApollyonEnchant extends VortexEnchant {

    public ApollyonEnchant() {
        super("apollyon", "Apollyon", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double afterHealth = victim.getHealth() - event.getFinalDamage();
        if (afterHealth > 0) return;
        if (isOnCooldown(shooter)) return;

        Location center = victim.getLocation();
        double radius = cfgd("radius", 5.0);
        int duration = cfgi("duration", 3 + level * 2);
        double hpPctPerSec = cfgd("hp_pct_per_sec", 0.05);

        new BukkitRunnable() {
            int ticks = 0;
            final int totalTicks = duration * 20;

            @Override
            public void run() {
                if (ticks >= totalTicks) {
                    cancel();
                    return;
                }
                if (ticks % 20 == 0) {
                    for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
                        if (entity.equals(shooter)) continue;
                        double dmg = entity.getHealth() * hpPctPerSec;
                        entity.damage(Math.max(dmg, 1.0), shooter);
                    }
                    ParticleUtil.drawCircle(center, radius, 24, Particle.SOUL_FIRE_FLAME);
                }
                if (ticks % 5 == 0) {
                    ParticleUtil.spawn(center.clone().add(0, 0.5, 0), Particle.SOUL, 3, radius * 0.5);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        ParticleUtil.burst(center, Particle.SOUL_FIRE_FLAME, 30, radius);
        SoundUtil.play(center, Sound.ENTITY_WITHER_SPAWN, 0.6f, 1.5f);

        setCooldownFromConfig(shooter, "cooldown", 15.0);
    }

    @Override
    public String getDescription(int level) {
        int dur = 3 + level * 2;
        return "§7Kill: §4§lDEATH ZONE §7— 5% HP/s for §e" + dur + "s §7in 5 blocks. 15s CD.";
    }
}

package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Napalm: Bolt impact creates a lingering fire zone for 3/5/7 seconds.
 * Entities in the zone take 1 damage per second.
 */
public class NapalmEnchant extends VortexEnchant {

    public NapalmEnchant() {
        super("napalm", "Napalm", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        Location center = victim.getLocation();
        double radius = cfgd("radius", 3.0);
        int duration = cfgi("duration", 1 + level * 2);
        double dps = cfgd("damage_per_second", 1.0);

        new BukkitRunnable() {
            int ticks = 0;
            final int totalTicks = duration * 20;

            @Override
            public void run() {
                if (ticks >= totalTicks) {
                    cancel();
                    return;
                }
                if (ticks % 10 == 0) {
                    ParticleUtil.drawCircle(center, radius, 12, Particle.FLAME);
                }
                if (ticks % 20 == 0) {
                    for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
                        if (entity.equals(shooter)) continue;
                        entity.damage(dps, shooter);
                        entity.setFireTicks(25);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        setCooldownFromConfig(shooter, "cooldown", 8.0);
    }

    @Override
    public String getDescription(int level) {
        int dur = 1 + level * 2;
        return "§7Bolt: §6lingering fire zone §7— §c1 DPS §7for §e" + dur + "s§7. 8s CD.";
    }
}

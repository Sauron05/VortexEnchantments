package com.vortexrpg.enchantments.enchant.impl.hammer;

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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Voidhammer: On kill, a void rift opens at the kill location for 4/6/8 seconds.
 * The rift pulls nearby entities inward + deals 2 DPS (damage per second).
 * Reality-tearing hammer strikes.
 */
public class VoidhammerEnchant extends VortexEnchant {

    public VoidhammerEnchant() {
        super("voidhammer", "Voidhammer", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int durationTicks = cfgi("duration_ticks", 40 + level * 40);
        double radius = cfgd("radius", 5.0);
        double dps = cfgd("dps", 2.0);
        Location center = victim.getLocation().clone();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 10;
                if (ticks > durationTicks) {
                    cancel();
                    return;
                }

                ParticleUtil.drawCircle(center, radius, 16, Particle.PORTAL);
                ParticleUtil.spawn(center.clone().add(0, 1, 0), Particle.REVERSE_PORTAL, 10, 1.0);

                for (LivingEntity e : MathUtil.getNearbyLiving(center, radius)) {
                    if (e.equals(killer)) continue;

                    // Pull toward center
                    Vector pull = center.toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.3);
                    e.setVelocity(e.getVelocity().add(pull));

                    // Damage
                    e.damage(dps * 0.5, killer); // 0.5 per half-second tick
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);

        SoundUtil.play(center, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.3f);
        ParticleUtil.spawn(center, Particle.EXPLOSION, 3, 0.5);
    }

    @Override
    public String getDescription(int level) {
        int dur = (40 + level * 40) / 20;
        return "§7Kill: §5void rift §7for §e" + dur + "s §7— pulls enemies + §c2 DPS§7.";
    }
}

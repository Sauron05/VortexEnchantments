package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Singularity: Creates a gravity vortex at the victim's location that pulls
 * all nearby entities toward it for 2/3/4 seconds, dealing tick damage.
 */
public class SingularityEnchant extends VortexEnchant {

    public SingularityEnchant() {
        super("singularity", "Singularity", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 12.0);
        double radius = cfgd("radius", 6.0) + (level - 1);
        int durationTicks = cfgi("duration_ticks", 40) + (level - 1) * 20;
        double pullStrength = cfgd("pull_strength", 0.4);
        double tickDamage = cfgd("tick_damage", 1.0);

        setCooldownSeconds(attacker, cooldown);
        Location center = victim.getLocation().clone();

        SoundUtil.play(center, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);

        BukkitTask[] task = new BukkitTask[1];
        final int[] ticks = {0};

        task[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (ticks[0]++ >= durationTicks / 2) {
                task[0].cancel();
                return;
            }

            ParticleUtil.drawCircle(center.clone().add(0, 0.5, 0), radius, 20, Particle.PORTAL);
            ParticleUtil.spawn(center, Particle.REVERSE_PORTAL, 10, radius * 0.5);

            for (Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
                if (e.equals(attacker) || !(e instanceof LivingEntity le)) continue;
                Vector pull = center.toVector().subtract(e.getLocation().toVector()).normalize().multiply(pullStrength);
                pull.setY(pull.getY() * 0.3);
                e.setVelocity(e.getVelocity().add(pull));
                if (ticks[0] % 10 == 0) {
                    le.damage(tickDamage, attacker);
                }
            }
        }, 0L, 2L);
    }

    @Override
    public String getDescription(int level) {
        int secs = 2 + (level - 1);
        return "§7Creates a §5gravity vortex§7 pulling enemies for §e" + secs + "s§7.";
    }
}

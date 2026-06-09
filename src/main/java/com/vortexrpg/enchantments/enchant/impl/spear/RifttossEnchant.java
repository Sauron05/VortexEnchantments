package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Rifttoss: When thrown trident hits, it creates a gravity rift at the
 * impact point that pulls all nearby entities inward for 3 seconds.
 */
public class RifttossEnchant extends VortexEnchant {

    public RifttossEnchant() {
        super("rifttoss", "Rifttoss", EnchantRarity.RARE, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        Location center = victim.getLocation();
        double radius = cfgd("radius", 2.0 + level);
        int durationTicks = cfgi("duration_ticks", 60);

        SoundUtil.play(center, Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 0.4f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 5;
                if (ticks > durationTicks) {
                    cancel();
                    return;
                }

                ParticleUtil.drawCircle(center, radius, 16, Particle.REVERSE_PORTAL);
                ParticleUtil.spawn(center.clone().add(0, 1, 0), Particle.PORTAL, 10, 0.5);

                for (var entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
                    if (entity.equals(shooter)) continue;
                    if (!(entity instanceof LivingEntity)) continue;

                    Vector pull = center.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(0.3);
                    entity.setVelocity(entity.getVelocity().add(pull));
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 5);

        setCooldownFromConfig(shooter, "cooldown", 15);
    }

    @Override
    public String getDescription(int level) {
        int radius = 2 + level;
        return "§7Thrown hit creates a §5gravity rift §7pulling mobs within §e" + radius + " blocks§7.";
    }
}

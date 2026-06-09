package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Predator: Arrow homes toward the last entity you damaged with an arrow.
 * Mild homing — adjusts trajectory slightly each tick toward the memorized target.
 */
public class PredatorEnchant extends VortexEnchant {

    private static final Map<UUID, UUID> LAST_TARGET = new HashMap<>();

    public PredatorEnchant() {
        super("predator", "Predator", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    public static void markTarget(UUID shooter, UUID target) {
        LAST_TARGET.put(shooter, target);
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        UUID targetId = LAST_TARGET.get(shooter.getUniqueId());
        if (targetId == null) return;

        if (event.getProjectile() instanceof AbstractArrow arrow) {
            double homingStrength = cfgd("homing", 0.02 + level * 0.02);

            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (arrow.isDead() || !arrow.isValid() || ticks++ > 60) {
                        cancel();
                        return;
                    }
                    LivingEntity target = MathUtil.getNearestLiving(arrow.getLocation(), 20,
                            e -> e.getUniqueId().equals(targetId));
                    if (target == null) {
                        cancel();
                        return;
                    }
                    Vector toTarget = target.getLocation().add(0, 1, 0).toVector()
                            .subtract(arrow.getLocation().toVector()).normalize();
                    Vector current = arrow.getVelocity();
                    double speed = current.length();
                    Vector adjusted = current.normalize().add(toTarget.multiply(homingStrength)).normalize().multiply(speed);
                    arrow.setVelocity(adjusted);

                    ParticleUtil.spawn(arrow.getLocation(), Particle.DUST_PLUME, 1, 0.1);
                }
            }.runTaskTimer(plugin, 5L, 1L);
        }
    }

    @Override
    public void onArrowHitEntity(org.bukkit.event.entity.EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        LAST_TARGET.put(shooter.getUniqueId(), victim.getUniqueId());
    }

    @Override
    public String getDescription(int level) {
        return "§7Arrow §chomes §7toward last damaged target.";
    }
}

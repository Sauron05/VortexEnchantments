package com.vortexrpg.enchantments.enchant.impl.crossbow;

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

import java.util.List;

/**
 * Heatseeker: Bolt curves mid-flight toward the nearest entity.
 * Mild auto-aim — adjusts trajectory slightly each tick.
 */
public class HeatseekerEnchant extends VortexEnchant {

    public HeatseekerEnchant() {
        super("heatseeker", "Heatseeker", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        if (event.getProjectile() instanceof AbstractArrow arrow) {
            double seekStrength = cfgd("seek_strength", 0.02 + level * 0.02);
            double seekRange = cfgd("seek_range", 8.0);

            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (arrow.isDead() || !arrow.isValid() || ticks++ > 40) {
                        cancel();
                        return;
                    }
                    LivingEntity target = MathUtil.getNearestLiving(arrow.getLocation(), seekRange,
                            e -> !e.equals(shooter));
                    if (target == null) return;

                    Vector toTarget = target.getLocation().add(0, 1, 0).toVector()
                            .subtract(arrow.getLocation().toVector()).normalize();
                    Vector current = arrow.getVelocity();
                    double speed = current.length();
                    Vector adjusted = current.normalize().add(toTarget.multiply(seekStrength)).normalize().multiply(speed);
                    arrow.setVelocity(adjusted);

                    ParticleUtil.spawn(arrow.getLocation(), Particle.FLAME, 1, 0.05);
                }
            }.runTaskTimer(plugin, 3L, 1L);
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Bolt §chomes §7toward nearest entity.";
    }
}

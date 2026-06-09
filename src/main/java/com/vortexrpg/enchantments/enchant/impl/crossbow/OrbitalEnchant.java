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
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Orbital: Bolt launches straight up, then crashes down from the sky onto the
 * nearest entity below. Devastating orbital strike.
 */
public class OrbitalEnchant extends VortexEnchant {

    public OrbitalEnchant() {
        super("orbital", "Orbital", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        if (event.getProjectile() instanceof AbstractArrow arrow) {
            // Redirect bolt straight up
            arrow.setVelocity(new Vector(0, 3.0, 0));

            double damage = cfgd("impact_damage", 6.0 + level * 3.0);
            double radius = cfgd("impact_radius", 3.0);

            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks++ > 40) {
                        cancel();
                        arrow.remove();
                        // Orbital strike
                        Location target = shooter.getLocation().clone();
                        LivingEntity nearest = MathUtil.getNearestLiving(
                                shooter.getLocation(), 15, e -> !e.equals(shooter));
                        if (nearest != null) {
                            target = nearest.getLocation();
                        }

                        Location impact = target.clone().add(0, 20, 0);

                        Arrow strike = target.getWorld().spawn(impact, Arrow.class);
                        strike.setShooter(shooter);
                        strike.setVelocity(new Vector(0, -4.0, 0));
                        strike.setDamage(damage);
                        strike.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (strike.isDead() || strike.isOnGround() || !strike.isValid()) {
                                    Location loc = strike.getLocation();
                                    for (LivingEntity e : MathUtil.getNearbyLiving(loc, radius)) {
                                        if (e.equals(shooter)) continue;
                                        e.damage(damage * 0.5, shooter);
                                    }
                                    ParticleUtil.burst(loc, Particle.EXPLOSION, 3, 1.0);
                                    SoundUtil.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
                                    cancel();
                                    return;
                                }
                                ParticleUtil.spawn(strike.getLocation(), Particle.FLAME, 3, 0.1);
                            }
                        }.runTaskTimer(plugin, 1L, 1L);
                        return;
                    }
                    ParticleUtil.spawn(arrow.getLocation(), Particle.END_ROD, 2, 0.1);
                }
            }.runTaskTimer(plugin, 1L, 1L);

            setCooldownFromConfig(shooter, "cooldown", 12.0);
        }
    }

    @Override
    public String getDescription(int level) {
        double dmg = 6 + level * 3;
        return "§7Bolt: §e§lORBITAL STRIKE §7— launches up, crashes down (§c" + dmg + " dmg§7). 12s CD.";
    }
}

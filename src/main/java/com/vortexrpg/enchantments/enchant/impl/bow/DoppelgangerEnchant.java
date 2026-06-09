package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Doppelganger: Arrow splits into 2 mid-flight after 10 blocks.
 * Each clone deals 50%/60%/70% of the original damage.
 */
public class DoppelgangerEnchant extends VortexEnchant {

    public DoppelgangerEnchant() {
        super("doppelganger", "Doppelganger", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        if (event.getProjectile() instanceof AbstractArrow arrow) {
            double dmgMult = cfgd("clone_damage_pct", 0.40 + level * 0.10);
            double splitDist = cfgd("split_distance", 10.0);

            new BukkitRunnable() {
                final Location start = shooter.getLocation();
                boolean split = false;

                @Override
                public void run() {
                    if (arrow.isDead() || !arrow.isValid() || split) {
                        cancel();
                        return;
                    }
                    if (arrow.getLocation().distance(start) >= splitDist) {
                        split = true;
                        spawnClone(arrow, shooter, dmgMult, 0.3);
                        spawnClone(arrow, shooter, dmgMult, -0.3);
                        ParticleUtil.spawn(arrow.getLocation(), Particle.END_ROD, 8, 0.3);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 2L, 1L);
        }
    }

    private void spawnClone(AbstractArrow original, Player shooter, double dmgMult, double offset) {
        Location loc = original.getLocation();
        Vector vel = original.getVelocity();
        Vector perp = new Vector(-vel.getZ(), 0, vel.getX()).normalize().multiply(offset);

        Arrow clone = loc.getWorld().spawn(loc.add(perp), Arrow.class);
        clone.setShooter(shooter);
        clone.setVelocity(vel);
        clone.setDamage(original.getDamage() * dmgMult);
        clone.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.40 + level * 0.10) * 100);
        return "§7Arrow §dsplits into 2 §7mid-flight (§c" + pct + "% §7dmg each).";
    }
}

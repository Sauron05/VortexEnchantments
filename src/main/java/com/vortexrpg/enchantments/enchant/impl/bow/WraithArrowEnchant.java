package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * WraithArrow: Arrow phases through blocks. On block hit, continues as a
 * spectral projectile that damages the first entity found on the other side.
 */
public class WraithArrowEnchant extends VortexEnchant {

    public WraithArrowEnchant() {
        super("wraitharrow", "Wraith Arrow", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitBlock(ProjectileHitEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        Location arrowLoc = event.getEntity().getLocation();
        Vector direction = event.getEntity().getVelocity().normalize();
        double damage = cfgd("phase_damage", 4.0 + level * 2.0);
        double searchDist = cfgd("search_distance", 3.0 + level * 2.0);

        event.getEntity().remove();

        new BukkitRunnable() {
            @Override
            public void run() {
                Location check = arrowLoc.clone();
                for (double d = 1.0; d <= searchDist; d += 0.5) {
                    check.add(direction.clone().multiply(0.5));
                    ParticleUtil.spawn(check, Particle.SOUL, 1, 0.1);

                    if (!check.getBlock().getType().isSolid()) {
                        LivingEntity target = MathUtil.getNearestLiving(check, 1.5, e -> !e.equals(shooter));
                        if (target != null) {
                            target.damage(damage, shooter);
                            ParticleUtil.spawn(target.getLocation().add(0, 1, 0), Particle.SOUL, 10, 0.4);
                            break;
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    @Override
    public String getDescription(int level) {
        double dmg = 4 + level * 2;
        return "§7Arrow §5phases through blocks§7, damaging first entity behind (§c" + dmg + "§7).";
    }
}

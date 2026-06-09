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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Airburst: Bolt detonates mid-flight after 1 second, dealing AoE damage
 * at its current position. Anti-air weapon.
 */
public class AirburstEnchant extends VortexEnchant {

    public AirburstEnchant() {
        super("airburst", "Airburst", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        if (event.getProjectile() instanceof AbstractArrow arrow) {
            int detonateDelay = cfgi("detonate_ticks", 20);
            double radius = cfgd("radius", 2.0 + level);
            double damage = cfgd("damage", 2.0 + level * 2.0);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.isDead() || !arrow.isValid()) return;

                    Location center = arrow.getLocation();
                    for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
                        if (entity.equals(shooter)) continue;
                        entity.damage(damage, shooter);
                    }

                    ParticleUtil.burst(center, Particle.EXPLOSION, 2, 0.5);
                    ParticleUtil.drawCircle(center, radius, 16, Particle.FLAME);
                    SoundUtil.play(center, Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 1.5f);
                    arrow.remove();
                }
            }.runTaskLater(plugin, detonateDelay);
        }
    }

    @Override
    public String getDescription(int level) {
        double dmg = 2 + level * 2;
        int r = (int) (2 + level);
        return "§7Bolt §cdetonates mid-flight §7— §c" + dmg + " AoE §7in §e" + r + " blocks§7.";
    }
}

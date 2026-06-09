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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Tripwire: Bolt embeds in a block and creates a proximity trap.
 * First entity to walk within 2 blocks takes 3/5/7 damage.
 */
public class TripwireEnchant extends VortexEnchant {

    public TripwireEnchant() {
        super("tripwire", "Tripwire", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitBlock(ProjectileHitEvent event, Player shooter, int level) {
        if (!isEnabled()) return;

        Location trapLoc = event.getEntity().getLocation();
        double radius = cfgd("trigger_radius", 2.0);
        double damage = cfgd("damage", 1.0 + level * 2.0);
        int lifetimeTicks = cfgi("lifetime_ticks", 200);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ > lifetimeTicks) {
                    cancel();
                    return;
                }
                if (ticks % 5 == 0) {
                    ParticleUtil.spawn(trapLoc, Particle.DUST_PLUME, 1, 0.2);
                }
                if (ticks % 4 == 0) {
                    for (LivingEntity entity : MathUtil.getNearbyLiving(trapLoc, radius)) {
                        if (entity.equals(shooter)) continue;
                        entity.damage(damage, shooter);
                        ParticleUtil.burst(trapLoc, Particle.CRIT, 10, radius);
                        SoundUtil.play(trapLoc, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.8f);
                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 1L);
    }

    @Override
    public String getDescription(int level) {
        double dmg = 1 + level * 2;
        return "§7Block hit: creates §eproximity trap §7— §c" + dmg + " damage §7to first passerby.";
    }
}

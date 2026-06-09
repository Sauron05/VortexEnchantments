package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Thread: Arrow connects a particle line between shooter and target for 3/4/5s. Target glows to shooter.
 */
public class ThreadEnchant extends VortexEnchant {

    private static final int[] THREAD_SECS = {3, 4, 5};

    public ThreadEnchant() {
        super("thread", "Thread", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int durationTicks = cfgi("thread_duration_seconds", THREAD_SECS[level - 1]) * 20;
        victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, durationTicks, 0, false, false, false));

        // Particle thread - drawn every tick
        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (!victim.isValid() || !shooter.isOnline()) { task.cancel(); return; }
            org.bukkit.Particle p = org.bukkit.Particle.INSTANT_EFFECT;
            int steps = 10;
            for (int i = 0; i <= steps; i++) {
                double t = (double) i / steps;
                double x = shooter.getLocation().getX() * (1 - t) + victim.getLocation().getX() * t;
                double y = shooter.getEyeLocation().getY() * (1 - t) + (victim.getLocation().getY() + 1) * t;
                double z = shooter.getLocation().getZ() * (1 - t) + victim.getLocation().getZ() * t;
                shooter.getWorld().spawnParticle(p, x, y, z, 1, 0, 0, 0, 0);
            }
        }, 0L, 2L);
    }

    @Override
    public String getDescription() { return "Arrow creates a particle tether between you and the target."; }

    @Override
    public String getDescription(int level) {
        return "§7Arrow hit: §etarget glows§7 and a particle line connects you for §e" + THREAD_SECS[level-1] + "s§7.";
    }
}

package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Arterial: Headshot or critical hit triggers massive bleed —
 * target takes 2 HP/s for 4/6/8 seconds. Devastating sustained damage.
 */
public class ArterialEnchant extends VortexEnchant {

    public ArterialEnchant() {
        super("arterial", "Arterial", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        boolean headshot = MathUtil.isHeadshot(victim, event.getDamager().getLocation(), 0.25);
        if (!headshot && !event.isCritical()) return;
        if (isOnCooldown(shooter)) return;

        double dps = cfgd("damage_per_second", 2.0);
        int duration = cfgi("duration", 2 + level * 2);

        new BukkitRunnable() {
            int ticks = 0;
            final int totalTicks = duration * 20;

            @Override
            public void run() {
                if (ticks >= totalTicks || victim.isDead()) {
                    cancel();
                    return;
                }
                if (ticks % 20 == 0) {
                    victim.damage(dps, shooter);
                    ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 4, 0.3);
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        SoundUtil.play(victim.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 0.5f);
        setCooldownFromConfig(shooter, "cooldown", 10.0);
    }

    @Override
    public String getDescription(int level) {
        int dur = 2 + level * 2;
        return "§7Headshot/crit: §4§lBLEED §c2 HP/s §7for §e" + dur + "s§7. 10s CD.";
    }
}

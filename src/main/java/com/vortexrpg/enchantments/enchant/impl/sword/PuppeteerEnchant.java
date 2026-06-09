package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Puppeteer: On hit, non-player mobs become friendly to you for 5/7/10 seconds,
 * targeting the nearest hostile entity instead of you.
 */
public class PuppeteerEnchant extends VortexEnchant {

    public PuppeteerEnchant() {
        super("puppeteer", "Puppeteer", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (victim instanceof Player) return;
        if (!(victim instanceof Mob mob)) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 20.0);
        int durationTicks = cfgi("duration_ticks", 100) + (level - 1) * 40;

        setCooldownSeconds(attacker, cooldown);
        event.setCancelled(true);

        ParticleUtil.spawn(mob.getLocation().add(0, 1.5, 0), Particle.ENCHANT, 20, 0.5);
        SoundUtil.play(mob.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.8f, 1.2f);
        attacker.sendMessage("§5[Puppeteer] §7You took control of §e" + mob.getName() + "§7!");

        BukkitTask[] task = new BukkitTask[1];
        final int[] ticks = {0};

        task[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (ticks[0]++ >= durationTicks / 4 || !mob.isValid() || mob.isDead()) {
                mob.setTarget(null);
                task[0].cancel();
                return;
            }

            LivingEntity nearest = null;
            double nearestDist = Double.MAX_VALUE;
            for (var e : mob.getNearbyEntities(16, 8, 16)) {
                if (e.equals(attacker) || e.equals(mob)) continue;
                if (!(e instanceof LivingEntity le) || e instanceof Player) continue;
                double dist = e.getLocation().distanceSquared(mob.getLocation());
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = le;
                }
            }
            if (nearest != null) {
                mob.setTarget(nearest);
            }

            ParticleUtil.spawn(mob.getLocation().add(0, 2, 0), Particle.ENCHANT, 3, 0.2);
        }, 0L, 4L);
    }

    @Override
    public String getDescription(int level) {
        int secs = 5 + (level - 1) * 2 + (level == 3 ? 1 : 0);
        return "§7Take §5control§7 of a mob for §e" + secs + "s§7. It fights for you!";
    }
}

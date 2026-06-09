package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Tether: Hit target is linked to you for 4/5/6 seconds.
 * If they move more than 15 blocks away, they're yanked back.
 */
public class TetherEnchant extends VortexEnchant {

    private static final int[] DURATIONS_TICKS = {80, 100, 120};

    public TetherEnchant() {
        super("tether", "Tether", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        int durationTicks = (int) MathUtil.secondsToTicks(cfg("tether_duration_seconds", DURATIONS_TICKS[level - 1] / 20.0));
        double maxDist = cfg("max_distance", 15.0);
        double yankDist = cfg("yank_distance", 10.0);

        plugin.getPlayerDataManager().setTetherTarget(attacker.getUniqueId(), victim.getUniqueId());

        BukkitTask[] taskRef = new BukkitTask[1];
        final int[] tick = {0};

        taskRef[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (tick[0]++ >= durationTicks / 2 || !attacker.isOnline() || !victim.isValid() || victim.isDead()) {
                plugin.getPlayerDataManager().clearTether(attacker.getUniqueId());
                taskRef[0].cancel();
                return;
            }

            double dist = attacker.getLocation().distance(victim.getLocation());
            // Particle line
            ParticleUtil.drawLine(attacker.getLocation().add(0, 1, 0),
                victim.getLocation().add(0, 1, 0), Particle.CRIT, 0.8);

            if (dist > maxDist) {
                // Yank target back toward attacker
                Location target = attacker.getLocation().add(
                    attacker.getLocation().getDirection().normalize().multiply(yankDist));
                victim.teleport(target);
                victim.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
            }
        }, 0L, 2L);
    }

    @Override
    public String getDescription() { return "Links hit target to you. If they flee too far, they're yanked back."; }

    @Override
    public String getDescription(int level) {
        int[] secs = {4, 5, 6};
        return "Tether target for §e" + secs[level - 1] + "s§7. If >15 blocks away: yanked to 10 blocks from you.";
    }
}

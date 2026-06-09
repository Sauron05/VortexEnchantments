package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Tether: Links you to the target via a soul strand — neither can
 * exceed 8/10/12 blocks distance for 5 seconds. If the target tries
 * to escape, they're pulled back.
 */
public class TetherEnchant extends VortexEnchant {

    public TetherEnchant() {
        super("tether", "Tether", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double maxDist = cfgd("max_distance", 6.0 + level * 2.0);
        int durationTicks = cfgi("duration_ticks", 100);

        ParticleUtil.drawLine(attacker.getLocation().add(0, 1, 0),
                victim.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 0.3);
        SoundUtil.play(attacker.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1.0f, 0.6f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 5;
                if (ticks > durationTicks || attacker.isDead() || victim.isDead()
                        || !attacker.isValid() || !victim.isValid()
                        || !attacker.getWorld().equals(victim.getWorld())) {
                    cancel();
                    return;
                }

                double dist = attacker.getLocation().distance(victim.getLocation());
                if (dist > maxDist) {
                    Vector pull = attacker.getLocation().toVector()
                            .subtract(victim.getLocation().toVector()).normalize().multiply(0.5);
                    victim.setVelocity(pull);
                }

                ParticleUtil.drawLine(attacker.getLocation().add(0, 1, 0),
                        victim.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 0.5);
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 5);

        setCooldownFromConfig(attacker, "cooldown", 12);
    }

    @Override
    public String getDescription(int level) {
        int dist = 6 + level * 2;
        return "§7Tethers target within §e" + dist + " blocks §7for §e5s§7.";
    }
}

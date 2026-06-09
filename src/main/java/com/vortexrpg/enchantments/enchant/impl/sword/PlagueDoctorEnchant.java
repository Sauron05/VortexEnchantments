package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Plague Doctor: On hit, infect target with a plague that damages over time
 * and spreads to nearby entities within 3/4/5 blocks.
 */
public class PlagueDoctorEnchant extends VortexEnchant {

    public PlagueDoctorEnchant() {
        super("plague_doctor", "Plague Doctor", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 8.0);
        double spreadRadius = cfgd("spread_radius", 3.0) + (level - 1);
        int plagueTicksTotal = cfgi("plague_duration_ticks", 80);
        double tickDamage = cfgd("tick_damage", 0.5 + level * 0.25);
        int spreadInterval = cfgi("spread_interval_ticks", 20);

        setCooldownSeconds(attacker, cooldown);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, plagueTicksTotal, 0, false, true));
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ITEM_SLIME, 10, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.6f, 0.5f);

        BukkitTask[] task = new BukkitTask[1];
        final int[] ticks = {0};

        task[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (ticks[0]++ >= plagueTicksTotal / spreadInterval || !victim.isValid() || victim.isDead()) {
                task[0].cancel();
                return;
            }

            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ITEM_SLIME, 5, 0.3);

            for (Entity e : victim.getNearbyEntities(spreadRadius, spreadRadius, spreadRadius)) {
                if (e.equals(attacker) || !(e instanceof LivingEntity le)) continue;
                if (le.hasPotionEffect(PotionEffectType.POISON)) continue;
                le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, plagueTicksTotal / 2, 0, false, true));
                le.damage(tickDamage, attacker);
                ParticleUtil.spawn(le.getLocation().add(0, 1, 0), Particle.ITEM_SLIME, 5, 0.3);
            }
        }, spreadInterval, spreadInterval);

        attacker.sendMessage("§2[Plague] §7Target infected! Plague is spreading...");
    }

    @Override
    public String getDescription(int level) {
        double radius = 3.0 + (level - 1);
        return "§7Infect target with §2plague§7 that spreads within §e" + String.format("%.0f", radius) + " blocks§7.";
    }
}

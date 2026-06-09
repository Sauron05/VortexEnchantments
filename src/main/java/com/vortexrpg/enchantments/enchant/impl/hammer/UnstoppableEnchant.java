package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Unstoppable: While sprinting, immune to speed-reducing effects + knockback + bonus damage.
 * Juggernaut mode — nothing slows you down.
 */
public class UnstoppableEnchant extends VortexEnchant {

    public UnstoppableEnchant() {
        super("unstoppable", "Unstoppable", EnchantRarity.EPIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!attacker.isSprinting()) return;

        double bonus = cfgd("bonus", 0.10 + level * 0.10);
        event.setDamage(event.getDamage() * (1.0 + bonus));

        // Cleanse slow effects
        attacker.removePotionEffect(PotionEffectType.SLOWNESS);
        attacker.removePotionEffect(PotionEffectType.MINING_FATIGUE);

        ParticleUtil.spawn(attacker.getLocation(), Particle.CLOUD, 6, 0.4);
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!victim.isSprinting()) return;

        // Negate knockback by resetting velocity after a tick
        org.bukkit.scheduler.BukkitRunnable task = new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                if (victim.isOnline() && victim.isSprinting()) {
                    victim.setVelocity(victim.getLocation().getDirection().multiply(0.3));
                }
            }
        };
        task.runTaskLater(plugin, 1L);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.10) * 100);
        return "§7Sprint: immune to slows/KB + §c+" + pct + "% §7damage.";
    }
}

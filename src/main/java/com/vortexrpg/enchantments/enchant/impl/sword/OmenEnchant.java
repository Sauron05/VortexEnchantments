package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Omen: Target gets warning particles. After 3/2.5/2s delay, receives the original hit damage again (true damage).
 */
public class OmenEnchant extends VortexEnchant {

    private static final double[] DELAY_SECS = {3.0, 2.5, 2.0};

    public OmenEnchant() {
        super("omen", "Omen", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double delaySecs = DELAY_SECS[level - 1];
        double echoPct = cfg("echo_damage_percent", 100.0) / 100.0;
        double originalDamage = event.getDamage() * echoPct;
        long delayTicks = MathUtil.secondsToTicks(delaySecs);

        // Warning particle effect
        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (victim.isDead() || !victim.isValid()) { task.cancel(); return; }
            ParticleUtil.spawn(victim.getLocation().add(0, victim.getHeight() + 0.3, 0),
                Particle.WITCH, 4, 0.25);
        }, 0L, 5L);

        // Delayed true damage
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!victim.isValid() || victim.isDead()) return;
            victim.damage(originalDamage, attacker);
        }, delayTicks);
    }

    @Override
    public String getDescription() { return "Target is warned with particles then takes the original hit's damage again after a delay."; }

    @Override
    public String getDescription(int level) {
        double delay = DELAY_SECS[level - 1];
        return "After §e" + delay + "s§7 delay: target takes original damage again (§ctrue damage§7).";
    }
}

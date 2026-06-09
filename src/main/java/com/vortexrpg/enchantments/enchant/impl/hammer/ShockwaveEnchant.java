package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Shockwave: Kill emits a knockback wave in 3/4/5 block radius.
 */
public class ShockwaveEnchant extends VortexEnchant {

    public ShockwaveEnchant() {
        super("shockwave", "Shockwave", EnchantRarity.COMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 2.0 + level);

        for (var entity : victim.getNearbyEntities(radius, radius, radius)) {
            if (entity.equals(killer)) continue;
            if (!(entity instanceof LivingEntity)) continue;

            Vector push = entity.getLocation().toVector()
                    .subtract(victim.getLocation().toVector()).normalize().multiply(1.0).setY(0.4);
            entity.setVelocity(push);
        }

        ParticleUtil.drawCircle(victim.getLocation(), radius, 20, Particle.SWEEP_ATTACK);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        int radius = 2 + level;
        return "§7Kill: §eknockback wave §7in §e" + radius + "-block §7radius.";
    }
}

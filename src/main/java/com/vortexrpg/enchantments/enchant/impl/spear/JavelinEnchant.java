package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Javelin: When thrown, the trident's damage increases +3/5/7% per block
 * traveled (capped at 20 blocks). Rewards long-range throws.
 */
public class JavelinEnchant extends VortexEnchant {

    public JavelinEnchant() {
        super("javelin", "Javelin", EnchantRarity.EPIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double dist = shooter.getLocation().distance(victim.getLocation());
        double maxDist = cfgd("max_distance", 20.0);
        double bonusPerBlock = cfgd("bonus_per_block", 0.01 + level * 0.02);

        double effectiveDist = Math.min(dist, maxDist);
        double bonus = effectiveDist * bonusPerBlock;

        event.setDamage(event.getDamage() * (1.0 + bonus));

        if (bonus > 0.3) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 15, 0.5);
        }
    }

    @Override
    public String getDescription(int level) {
        int pctPerBlock = (int) ((0.01 + level * 0.02) * 100);
        return "§7Thrown damage: §c+" + pctPerBlock + "% §7per block traveled (max §e20§7).";
    }
}

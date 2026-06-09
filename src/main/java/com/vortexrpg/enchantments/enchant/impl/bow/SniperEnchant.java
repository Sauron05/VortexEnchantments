package com.vortexrpg.enchantments.enchant.impl.bow;

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
 * Sniper: Damage increases by 3/5/7% per 5 blocks of arrow travel distance.
 * Rewards patient, long-range shots.
 */
public class SniperEnchant extends VortexEnchant {

    public SniperEnchant() {
        super("sniper", "Sniper", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double distance = shooter.getLocation().distance(victim.getLocation());
        double perFiveBlocks = cfgd("bonus_per_5", 0.01 + level * 0.02);
        double bonus = (distance / 5.0) * perFiveBlocks;
        double maxBonus = cfgd("max_bonus", 1.0);

        bonus = Math.min(bonus, maxBonus);
        event.setDamage(event.getDamage() * (1.0 + bonus));

        if (bonus > 0.2) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.CRIT, (int) (bonus * 20), 0.3);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.01 + level * 0.02) * 100);
        return "§7+" + pct + "% damage per §e5 blocks §7of distance §8(max +100%).";
    }
}

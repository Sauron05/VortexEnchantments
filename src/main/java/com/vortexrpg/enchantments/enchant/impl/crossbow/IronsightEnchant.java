package com.vortexrpg.enchantments.enchant.impl.crossbow;

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
 * Ironsight: Standing still when the bolt hits grants 10/15/20% bonus damage.
 * Rewards precise, planted shots.
 */
public class IronsightEnchant extends VortexEnchant {

    public IronsightEnchant() {
        super("ironsight", "Ironsight", EnchantRarity.COMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        if (shooter.getVelocity().lengthSquared() > 0.01) return;

        double bonus = cfgd("bonus", 0.05 + level * 0.05);
        event.setDamage(event.getDamage() * (1.0 + bonus));
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.CRIT, 6, 0.2);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7Stationary shots: §e+" + pct + "% §7damage.";
    }
}

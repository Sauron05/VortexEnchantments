package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/**
 * Cascade: Kill sends a shockwave dealing 50/65/80% of the lethal damage
 * to enemies within 4 blocks, with diminishing reach.
 */
public class CascadeEnchant extends VortexEnchant {

    public CascadeEnchant() {
        super("cascade", "Cascade", EnchantRarity.EPIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 4.0);
        double basePct = cfgd("base_percent", 0.35 + level * 0.15);

        double shockDmg = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue() * basePct;

        ParticleUtil.drawCircle(victim.getLocation(), radius, 20, Particle.SWEEP_ATTACK);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 0.7f);

        for (LivingEntity le : MathUtil.getNearbyLiving(victim.getLocation(), radius)) {
            if (le.equals(killer)) continue;
            double dist = le.getLocation().distance(victim.getLocation());
            double falloff = 1.0 - (dist / radius);
            le.damage(shockDmg * Math.max(falloff, 0.1), killer);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.35 + level * 0.15) * 100);
        return "§7Kill shockwave: §c" + pct + "% §7lethal damage to nearby enemies.";
    }
}

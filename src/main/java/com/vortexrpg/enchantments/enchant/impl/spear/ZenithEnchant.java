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
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Zenith: Attacks while falling from 4+ blocks deal +50/75/100% bonus damage
 * and create an AoE shockwave on impact. A devastating aerial plunge attack.
 */
public class ZenithEnchant extends VortexEnchant {

    public ZenithEnchant() {
        super("zenith", "Zenith", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double fallDist = attacker.getFallDistance();
        if (fallDist < 4.0) return;

        double bonusPct = cfgd("bonus_percent", 0.25 + level * 0.25);
        double aoePct = cfgd("aoe_percent", 0.50);
        double aoeRadius = cfgd("aoe_radius", 3.0);

        // Scale bonus with fall distance (capped at 15 blocks)
        double scaledBonus = bonusPct * Math.min(fallDist / 4.0, 3.0);
        event.setDamage(event.getDamage() * (1.0 + scaledBonus));

        // AoE shockwave
        double aoeDmg = event.getDamage() * aoePct;
        for (LivingEntity le : MathUtil.getNearbyLiving(victim.getLocation(), aoeRadius)) {
            if (le.equals(victim) || le.equals(attacker)) continue;
            le.damage(aoeDmg, attacker);
        }

        ParticleUtil.drawCircle(victim.getLocation(), aoeRadius, 20, Particle.SWEEP_ATTACK);
        ParticleUtil.spawn(victim.getLocation(), Particle.EXPLOSION, 3, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.5f);

        // Reset fall damage since we used it
        attacker.setFallDistance(0);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.25 + level * 0.25) * 100);
        return "§7Aerial strikes (4+ blocks): §c+" + pct + "% §7damage + §eAoE shockwave§7.";
    }
}

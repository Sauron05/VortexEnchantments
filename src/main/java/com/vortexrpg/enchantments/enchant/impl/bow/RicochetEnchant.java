package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Ricochet: Arrows bounce from the target to 1/2/3 nearby enemies,
 * dealing 50% of the original damage each.
 */
public class RicochetEnchant extends VortexEnchant {

    public RicochetEnchant() {
        super("ricochet", "Ricochet", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int bounces = cfgi("bounces", level);
        double bounceDmg = event.getDamage() * cfgd("bounce_damage_pct", 0.50);
        double range = cfgd("bounce_range", 5.0);

        List<LivingEntity> nearby = MathUtil.getNearbyLiving(victim.getLocation(), range);
        nearby.removeIf(e -> e.equals(victim) || e.equals(shooter));

        int count = 0;
        LivingEntity prev = victim;
        for (LivingEntity target : nearby) {
            if (count >= bounces) break;
            target.damage(bounceDmg, shooter);
            ParticleUtil.drawLine(prev.getLocation().add(0, 1, 0),
                    target.getLocation().add(0, 1, 0), Particle.CRIT, 0.3);
            prev = target;
            count++;
        }
    }

    @Override
    public String getDescription(int level) {
        return "§7Arrow bounces to §e" + level + " §7nearby targets for §c50% §7damage.";
    }
}

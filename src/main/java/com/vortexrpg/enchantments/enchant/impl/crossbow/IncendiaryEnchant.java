package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Incendiary: Bolt sets a 2x2/3x3/4x4 area on fire around the impact point
 * and ignites the primary target.
 */
public class IncendiaryEnchant extends VortexEnchant {

    public IncendiaryEnchant() {
        super("incendiary", "Incendiary", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int fireTicks = cfgi("fire_ticks", 60 + level * 20);
        double radius = cfgd("radius", 1.0 + level);
        victim.setFireTicks(fireTicks);

        Location center = victim.getLocation();
        for (LivingEntity nearby : MathUtil.getNearbyLiving(center, radius)) {
            if (nearby.equals(shooter)) continue;
            nearby.setFireTicks(fireTicks / 2);
        }

        ParticleUtil.drawCircle(center, radius, 12, Particle.FLAME);
        ParticleUtil.spawn(center, Particle.LAVA, 6, 0.5);
    }

    @Override
    public String getDescription(int level) {
        int r = 1 + level;
        return "§7Bolt §6ignites §7target + §6fire area §7(" + r + " block radius).";
    }
}

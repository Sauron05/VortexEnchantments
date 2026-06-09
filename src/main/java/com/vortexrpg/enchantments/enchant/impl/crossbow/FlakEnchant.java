package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Flak: Bolt explodes 1 block before target dealing damage in a cone radius of 2/3/4 blocks.
 */
public class FlakEnchant extends VortexEnchant {
    private static final double[] RADIUS = {2.0, 3.0, 4.0};
    public FlakEnchant() { super("flak", "Flak", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        if (!isEnabled()) return;
        double radius = cfg("radius_" + level, RADIUS[level-1]);
        double damage = cfg("fragment_damage_" + level, 3.0 + level);
        // Explode 1 block before target
        Location flakLoc = target.getLocation().clone();
        if (event.getDamager() instanceof AbstractArrow arrow) {
            Vector vel = arrow.getVelocity().normalize();
            flakLoc = arrow.getLocation().subtract(vel.multiply(1.0));
        }
        ParticleUtil.burst(flakLoc, Particle.EXPLOSION, 12, 0.5);
        SoundUtil.play(flakLoc, Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.5f);
        for (LivingEntity nearby : MathUtil.getNearbyLiving(flakLoc, radius)) {
            if (nearby.equals(shooter)) continue;
            nearby.damage(damage, shooter);
        }
    }

    @Override public String getDescription() { return "Bolts explode before impact, damaging nearby entities."; }
    @Override public String getDescription(int level) {
        return "§7Bolt explodes §e1 block§7 before target; §c" + (int)RADIUS[level-1] + " block§7 fragment radius."; }
}

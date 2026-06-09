package com.vortexrpg.enchantments.enchant.impl.bow;

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
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Downdraft: Arrow impact creates a downward gust, yanking flying/airborne
 * entities to the ground within a 4/5/6 block radius.
 */
public class DowndraftEnchant extends VortexEnchant {

    public DowndraftEnchant() {
        super("downdraft", "Downdraft", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 3.0 + level);
        double pullForce = cfgd("pull_force", 0.6 + level * 0.2);
        Location center = victim.getLocation();

        for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
            if (entity.equals(shooter)) continue;
            if (!entity.isOnGround()) {
                entity.setVelocity(new Vector(0, -pullForce, 0));
                entity.setFallDistance(0);
            }
        }

        ParticleUtil.spawn(center.add(0, 3, 0), Particle.CLOUD, 15, radius);
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (3 + level);
        return "§7Pull airborne entities §cdown §7in §e" + r + " block §7radius.";
    }
}

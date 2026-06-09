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
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Gale: Bolt impact pushes away all entities from the impact point in 3/4/5 radius.
 * Wind blast — an AoE knockback explosion.
 */
public class GaleEnchant extends VortexEnchant {

    public GaleEnchant() {
        super("gale", "Gale", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 2.0 + level);
        double force = cfgd("force", 0.5 + level * 0.2);
        Location center = victim.getLocation();

        for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
            if (entity.equals(shooter)) continue;
            Vector push = entity.getLocation().toVector().subtract(center.toVector()).normalize().multiply(force).setY(0.3);
            entity.setVelocity(push);
        }

        ParticleUtil.drawCircle(center, radius, 16, Particle.CLOUD);
        ParticleUtil.spawn(center, Particle.EXPLOSION, 1, 0.1);
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (2 + level);
        return "§7Bolt: §bwind blast §7pushes entities in §e" + r + " block §7radius.";
    }
}

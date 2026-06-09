package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Collapsar: Hit pulls all entities within 4/6/8 blocks toward impact point.
 * Gravitational implosion — cluster your enemies together.
 */
public class CollapsarEnchant extends VortexEnchant {

    public CollapsarEnchant() {
        super("collapsar", "Collapsar", EnchantRarity.EPIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 2.0 + level * 2.0);
        Location center = victim.getLocation();

        for (var entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity.equals(attacker) || entity.equals(victim)) continue;
            if (!(entity instanceof LivingEntity)) continue;

            Vector pull = center.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(0.8).setY(0.2);
            entity.setVelocity(pull);
        }

        ParticleUtil.drawCircle(center, radius, 20, Particle.ENCHANTED_HIT);
        SoundUtil.play(center, Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 0.5f);
    }

    @Override
    public String getDescription(int level) {
        int r = 2 + level * 2;
        return "§7Hit: §dpulls §7all entities within §e" + r + " blocks §7to impact point.";
    }
}

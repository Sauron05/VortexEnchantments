package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Helix Throw: Thrown trident spirals, damaging all entities within
 * 2/3/4 blocks of the flight path. Uses onArrowHitEntity as the trigger
 * and AoEs around the impact point.
 */
public class HelixThrowEnchant extends VortexEnchant {

    public HelixThrowEnchant() {
        super("helixthrow", "Helix Throw", EnchantRarity.RARE, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 1.0 + level);
        double aoePct = cfgd("aoe_percent", 0.40);
        double aoeDmg = event.getDamage() * aoePct;

        Location impact = victim.getLocation();
        ParticleUtil.spawnHelix(impact, Particle.ENCHANTED_HIT, 3, 2.0);
        SoundUtil.play(impact, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.9f, 1.3f);

        for (LivingEntity nearby : MathUtil.getNearbyLiving(impact, radius)) {
            if (nearby.equals(victim) || nearby.equals(shooter)) continue;
            nearby.damage(aoeDmg, shooter);
        }
    }

    @Override
    public String getDescription(int level) {
        int radius = 1 + level;
        return "§7Thrown hit spirals, dealing §c40% §7AoE in §e" + radius + " block §7radius.";
    }
}

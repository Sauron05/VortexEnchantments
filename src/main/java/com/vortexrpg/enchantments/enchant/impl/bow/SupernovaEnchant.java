package com.vortexrpg.enchantments.enchant.impl.bow;

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
 * Supernova: On critical arrow hit, creates an expanding damage sphere —
 * deals 3/5/7 damage to all entities in 5-block radius.
 */
public class SupernovaEnchant extends VortexEnchant {

    public SupernovaEnchant() {
        super("supernova", "Supernova", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!event.isCritical()) return;
        if (isOnCooldown(shooter)) return;

        Location center = victim.getLocation();
        double radius = cfgd("radius", 5.0);
        double damage = cfgd("damage", 1.0 + level * 2.0);

        for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
            if (entity.equals(shooter)) continue;
            entity.damage(damage, shooter);
        }

        ParticleUtil.drawCircle(center, radius, 24, Particle.END_ROD);
        ParticleUtil.burst(center.add(0, 1, 0), Particle.EXPLOSION, 3, 1.0);
        SoundUtil.play(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);

        setCooldownFromConfig(shooter, "cooldown", 6.0);
    }

    @Override
    public String getDescription(int level) {
        double dmg = 1 + level * 2;
        return "§7Critical: §6§lSUPERNOVA §7— §c" + dmg + " AoE §7in 5-block sphere. 6s CD.";
    }
}

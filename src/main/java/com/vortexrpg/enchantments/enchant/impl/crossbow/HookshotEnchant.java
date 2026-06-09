package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Hookshot: Bolt pulls the hit target toward the shooter.
 * Harpoon-like mechanic — yank enemies closer.
 */
public class HookshotEnchant extends VortexEnchant {

    public HookshotEnchant() {
        super("hookshot", "Hookshot", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double pullForce = cfgd("pull_force", 0.4 + level * 0.2);
        Vector toShooter = shooter.getLocation().toVector().subtract(victim.getLocation().toVector()).normalize();
        toShooter.multiply(pullForce).setY(0.25);
        victim.setVelocity(toShooter);

        ParticleUtil.drawLine(victim.getLocation().add(0, 1, 0),
                shooter.getLocation().add(0, 1, 0), Particle.CRIT, 0.4);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 0.8f, 0.6f);
    }

    @Override
    public String getDescription(int level) {
        return "§7Bolt §epulls target §7toward you.";
    }
}

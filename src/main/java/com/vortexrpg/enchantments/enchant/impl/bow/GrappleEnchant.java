package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Grapple: Arrow that hits a block pulls the shooter to the landing location.
 * Mobility/traversal enchant — swing to where your arrow lands.
 */
public class GrappleEnchant extends VortexEnchant {

    public GrappleEnchant() {
        super("grapple", "Grapple", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitBlock(ProjectileHitEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        Location arrowLoc = event.getEntity().getLocation();
        Location playerLoc = shooter.getLocation();
        double dist = playerLoc.distance(arrowLoc);

        double maxRange = cfgd("max_range", 30.0 + level * 10);
        if (dist > maxRange) return;

        Vector direction = arrowLoc.toVector().subtract(playerLoc.toVector()).normalize();
        double speed = cfgd("speed", 0.8 + level * 0.3);
        direction.multiply(Math.min(speed, dist * 0.15));
        direction.setY(Math.max(direction.getY(), 0.3));

        shooter.setVelocity(direction);
        shooter.setFallDistance(0);

        ParticleUtil.drawLine(playerLoc.add(0, 1, 0), arrowLoc, Particle.END_ROD, 0.5);
        SoundUtil.play(playerLoc, Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1.0f, 0.8f);

        setCooldownFromConfig(shooter, "cooldown", 4.0);
    }

    @Override
    public String getDescription(int level) {
        int range = (int) (30 + level * 10);
        return "§7Block hit: §egrapple §7to arrow landing (max §e" + range + " blocks§7).";
    }
}

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Leviathan: Bolt creates a water-like vortex at impact, dragging entities
 * toward the center and slowing them. Oceanic might.
 */
public class LeviathanEnchant extends VortexEnchant {

    public LeviathanEnchant() {
        super("leviathan", "Leviathan", EnchantRarity.EPIC, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        double radius = cfgd("radius", 3.0 + level);
        double pullForce = cfgd("pull_force", 0.3 + level * 0.1);
        Location center = victim.getLocation();

        for (LivingEntity entity : MathUtil.getNearbyLiving(center, radius)) {
            if (entity.equals(shooter)) continue;
            Vector pull = center.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(pullForce);
            entity.setVelocity(pull.setY(0.1));
        }

        ParticleUtil.drawCircle(center, radius, 20, Particle.SPLASH);
        ParticleUtil.spawnHelix(center, Particle.DRIPPING_WATER, 8, 2.0);
        SoundUtil.play(center, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 0.8f, 0.5f);

        setCooldownFromConfig(shooter, "cooldown", 8.0);
    }

    @Override
    public String getDescription(int level) {
        int r = (int) (3 + level);
        return "§7Bolt: §b§lVORTEX §7— drags entities in §e" + r + " blocks §7toward center. 8s CD.";
    }
}

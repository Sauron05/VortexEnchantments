package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Dimension Slash: Attack sends a rift-blade forward up to 10/15/20 blocks,
 * damaging every entity in its path (passes through blocks).
 */
public class DimensionSlashEnchant extends VortexEnchant {

    public DimensionSlashEnchant() {
        super("dimension_slash", "Dimension Slash", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 8.0);
        double range = cfgd("range", 10.0) + (level - 1) * 5.0;
        double slashDamage = cfgd("slash_damage", 3.0 + level);
        double hitRadius = cfgd("hit_radius", 1.5);

        setCooldownSeconds(attacker, cooldown);

        Vector direction = attacker.getLocation().getDirection().normalize();
        Location start = attacker.getEyeLocation().clone();

        SoundUtil.play(start, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 0.5f);

        for (double d = 1; d <= range; d += 0.5) {
            Location point = start.clone().add(direction.clone().multiply(d));
            ParticleUtil.spawn(point, Particle.REVERSE_PORTAL, 3, 0.2);

            for (Entity e : point.getWorld().getNearbyEntities(point, hitRadius, hitRadius, hitRadius)) {
                if (e.equals(attacker) || !(e instanceof LivingEntity le)) continue;
                if (e.equals(victim)) continue;
                le.damage(slashDamage, attacker);
                ParticleUtil.spawn(le.getLocation().add(0, 1, 0), Particle.CRIT, 5, 0.3);
            }
        }
    }

    @Override
    public String getDescription(int level) {
        int range = 10 + (level - 1) * 5;
        return "§7Sends a §5dimensional rift§7 forward §e" + range + " blocks§7, damaging all in its path.";
    }
}

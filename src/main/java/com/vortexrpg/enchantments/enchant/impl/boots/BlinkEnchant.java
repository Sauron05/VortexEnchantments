package com.vortexrpg.enchantments.enchant.impl.boots;

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
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

/**
 * Blink: Sneak to teleport forward, damaging all enemies in the path.
 */
public class BlinkEnchant extends VortexEnchant {
    public BlinkEnchant() { super("blink", "Blink", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        double dist = cfgd("distance", 6.0 + level * 2);
        double dmg = cfgd("path_damage", 3.0 * level);
        Location start = player.getLocation().clone();
        Location end = start.clone().add(player.getLocation().getDirection().multiply(dist));

        // Damage enemies in path
        for (LivingEntity e : MathUtil.getNearbyLiving(start, dist + 2)) {
            if (e.equals(player)) continue;
            Location eLoc = e.getLocation();
            double distToLine = distPointToLine(start, end, eLoc);
            if (distToLine <= 2.0) {
                e.damage(dmg, player);
            }
        }

        ParticleUtil.drawLine(start, end, Particle.PORTAL, 0.3);
        player.teleport(end);
        ParticleUtil.burst(end, Particle.REVERSE_PORTAL, 15, 1.0);
        SoundUtil.play(start, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.2f);
        SoundUtil.play(end, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.2f);
        setCooldownFromConfig(player, "cooldown", 20.0);
    }

    private double distPointToLine(Location start, Location end, Location point) {
        org.bukkit.util.Vector line = end.toVector().subtract(start.toVector());
        org.bukkit.util.Vector toPoint = point.toVector().subtract(start.toVector());
        double t = Math.max(0, Math.min(1, toPoint.dot(line) / line.lengthSquared()));
        org.bukkit.util.Vector closest = start.toVector().add(line.multiply(t));
        return closest.distance(point.toVector());
    }

    @Override public String getDescription(int level) {
        return "§7Sneak: §5blink §7forward " + (6 + level * 2) + " blocks, damaging enemies. §820s CD.";
    }
}

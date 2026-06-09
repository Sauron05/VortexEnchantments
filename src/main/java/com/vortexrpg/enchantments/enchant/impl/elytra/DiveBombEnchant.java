package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * DiveBomb — Elytra (Epic, Max 3)
 * Diving downward at speed > threshold triggers a ground-slam on landing, dealing AoE damage.
 */
@SuppressWarnings("deprecation")
public class DiveBombEnchant extends VortexEnchant {

    public DiveBombEnchant() {
        super("dive_bomb", "DiveBomb", "elytra");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        double[] dmg = {6, 9, 14};
        return "Diving steeply and landing deals §c" + (int)dmg[level - 1] + "§7 AoE damage in a 5-block radius.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (player.isGliding()) {
            Vector vel = player.getVelocity();
            if (vel.getY() < -cfgd("min_dive_speed", 0.8)) {
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "divebomb_armed", 1);
            }
        } else {
            // Just landed
            int armed = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "divebomb_armed", 0);
            if (armed == 1 && player.isOnGround()) {
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "divebomb_armed", 0);
                double[] dmg = {6, 9, 14};
                double damage = cfgd("damage", dmg[level - 1]);
                double radius = cfgd("radius", 5.0);
                player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 6, 1, 0, 1, 0);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 0.8f);
                for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                    if (e == player || !(e instanceof LivingEntity le)) continue;
                    le.damage(damage, player);
                }
            }
        }
    }
}

package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Neurolink: Shares X% of XP picked up with the nearest ally within 8 blocks.
 */
public class NeurolinkEnchant extends VortexEnchant {
    public NeurolinkEnchant() { super("neurolink", "Neurolink", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Passively indicated by particles; XP sharing handled via XP event
        double radius = cfgd("radius", 8.0);
        Player nearest = null;
        double closest = radius;
        for (org.bukkit.entity.Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof Player p && !p.equals(player)) {
                double dist = p.getLocation().distance(player.getLocation());
                if (dist < closest) {
                    closest = dist;
                    nearest = p;
                }
            }
        }
        if (nearest != null) {
            com.vortexrpg.enchantments.util.ParticleUtil.drawLine(
                    player.getLocation().add(0, 2, 0), nearest.getLocation().add(0, 2, 0),
                    org.bukkit.Particle.ELECTRIC_SPARK, 0.5);
        }
    }

    @Override public String getDescription(int level) {
        int pct = 10 + level * 10;
        return "§7Shares §a" + pct + "%§7 of XP with nearest ally.";
    }
}

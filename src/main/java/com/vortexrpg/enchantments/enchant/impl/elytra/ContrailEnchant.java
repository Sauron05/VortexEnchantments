package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Contrail — Elytra (Common, Max 3)
 * While gliding, leaves a decorative particle trail. Additional bonus: minor drag reduction (speed boost).
 */
public class ContrailEnchant extends VortexEnchant {

    public ContrailEnchant() {
        super("contrail", "Contrail", "elytra");
    }

    @Override
    public String getTier() { return "COMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "Leaves a §bparticle trail§7 while gliding, slightly reducing air drag.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        Particle[] particles = {Particle.CLOUD, Particle.WITCH, Particle.END_ROD};
        Particle p = particles[Math.min(level - 1, particles.length - 1)];
        player.getWorld().spawnParticle(p, player.getLocation(), 3, 0.1, 0.1, 0.1, 0.005);

        // Minor speed bonus
        Vector vel = player.getVelocity();
        if (vel.length() > 0.3) {
            double boost = cfgd("speed_bonus", 0.01 * level);
            player.setVelocity(vel.multiply(1.0 + boost));
        }
    }
}

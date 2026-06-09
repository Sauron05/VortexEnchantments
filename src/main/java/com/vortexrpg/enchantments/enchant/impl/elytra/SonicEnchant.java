package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Sonic — Elytra (Epic, Max 3)
 * While gliding at high speed (velocity > threshold), collision/impact damage deals bonus area damage.
 */
public class SonicEnchant extends VortexEnchant {

    public SonicEnchant() {
        super("sonic", "Sonic", "elytra");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        double[] bonus = {30, 50, 80};
        return "Gliding into entities at high speed deals §c+" + (int)bonus[level - 1] + "%§7 bonus area damage.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        double speed = player.getVelocity().length();
        double minSpeed = cfgd("min_speed", 0.8);
        if (speed < minSpeed) return;

        double radius = cfgd("aoe_radius", 2.5);
        double[] bonuses = {0.30, 0.50, 0.80};
        double bonus = cfgd("bonus", bonuses[level - 1]);

        // Damage nearby entities player is flying through
        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius * 0.5, radius)) {
            if (entity == player) continue;
            if (!(entity instanceof LivingEntity le)) continue;
            double dmg = speed * bonus * 2.0;
            le.damage(dmg, player);
        }
    }
}

package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

/**
 * Capacitor — Elytra (Epic, Max 3)
 * Accumulates charge while gliding; on landing deals an electric burst that damages nearby mobs.
 */
@SuppressWarnings("deprecation")
public class CapacitorElytraEnchant extends VortexEnchant {

    public CapacitorElytraEnchant() {
        super("capacitor_elytra", "Capacitor", "elytra");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "Builds charge while gliding; §clightning burst§7 on landing damages nearby enemies.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (player.isGliding()) {
            double charge = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "capacitor_elytra_charge", 0);
            double ratePerTick = cfgd("charge_rate", 0.05 * level);
            double maxCharge = cfgd("max_charge", 10.0);
            charge = Math.min(charge + ratePerTick, maxCharge);
            plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "capacitor_elytra_charge", charge);
        } else if (player.isOnGround()) {
            double charge = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "capacitor_elytra_charge", 0);
            if (charge > 1.0) {
                plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "capacitor_elytra_charge", 0);
                double radius = cfgd("burst_radius", 4.0);
                player.getWorld().strikeLightningEffect(player.getLocation());
                for (org.bukkit.entity.Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                    if (e == player || !(e instanceof org.bukkit.entity.LivingEntity le)) continue;
                    le.damage(charge * 0.8, player);
                }
            }
        }
    }
}

package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

/**
 * MantleShift — Elytra (Epic, Max 1)
 * Sneaking while gliding toggles the elytra's appearance between "cape" and "wings" cosmetic modes.
 * Mechanically: buffers glide momentum into a burst of speed when wings are opened.
 */
public class MantleShiftEnchant extends VortexEnchant {

    public MantleShiftEnchant() {
        super("mantle_shift", "MantleShift", "elytra");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 1; }

    @Override
    public String getDescription(int level) {
        return "§eSneaking§7 while in free-fall stores momentum; opening your elytra converts it to a speed burst.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) {
            // Accumulate momentum while falling (not gliding)
            double vy = player.getVelocity().getY();
            if (vy < 0) {
                double stored = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "mantleshift_stored", 0);
                stored += Math.abs(vy) * 0.5;
                stored = Math.min(stored, cfgd("max_stored", 5.0));
                plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "mantleshift_stored", stored);
            }
        } else {
            double stored = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "mantleshift_stored", 0);
            if (stored > 0.5) {
                // Convert to forward burst on glide open
                plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "mantleshift_stored", 0);
                org.bukkit.util.Vector dir = player.getLocation().getDirection().normalize().multiply(stored * 0.4);
                player.setVelocity(dir);
            }
        }
    }
}

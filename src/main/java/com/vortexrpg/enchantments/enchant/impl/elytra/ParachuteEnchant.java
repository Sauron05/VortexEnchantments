package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Parachute — Elytra (Common, Max 3)
 * If you free-fall more than 20 blocks, the elytra auto-opens (sets gliding) to prevent death.
 */
@SuppressWarnings("deprecation")
public class ParachuteEnchant extends VortexEnchant {

    public ParachuteEnchant() {
        super("parachute", "Parachute", "elytra");
    }

    @Override
    public String getTier() { return "COMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] thresh = {20, 15, 10};
        return "Auto-deploys glide after falling §e" + thresh[level - 1] + "§7 blocks to prevent death.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (player.isGliding() || player.isOnGround()) return;
        double fallDist = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "parachute_fall", 0);
        Vector vel = player.getVelocity();
        if (vel.getY() < 0) {
            fallDist += Math.abs(vel.getY());
            plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "parachute_fall", fallDist);
        } else {
            plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "parachute_fall", 0);
        }

        int[] thresholds = {20, 15, 10};
        double threshold = cfgd("fall_threshold", thresholds[level - 1]);
        if (fallDist >= threshold) {
            plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "parachute_fall", 0);
            player.setGliding(true);
            // Give upward nudge to break fall
            Vector nudge = player.getVelocity().clone();
            nudge.setY(0.4);
            player.setVelocity(nudge);
            player.playSound(player.getLocation(), Sound.ITEM_ELYTRA_FLYING, 1f, 1f);
        }
    }
}

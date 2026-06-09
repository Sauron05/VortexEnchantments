package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Tailwind — Elytra (Uncommon, Max 3)
 * When you launch from the ground into a glide, receive a directional velocity boost.
 */
public class TailwindEnchant extends VortexEnchant {

    public TailwindEnchant() {
        super("tailwind", "Tailwind", "elytra");
    }

    @Override
    public String getTier() { return "UNCOMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        double[] boosts = {0.3, 0.5, 0.8};
        return "Launching into a glide gives a §a+" + (int)(boosts[level - 1] * 100) + "%§7 velocity boost.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        // Detect glide-start: was on ground last tick, now gliding
        boolean wasGliding = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "tailwind_gliding", 0) == 1;
        boolean isGliding = player.isGliding();

        if (!wasGliding && isGliding) {
            // Just started gliding
            double[] boosts = {0.3, 0.5, 0.8};
            double boost = cfgd("launch_boost", boosts[level - 1]);
            Vector dir = player.getLocation().getDirection().normalize();
            player.setVelocity(dir.multiply(boost + 0.5));
        }

        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "tailwind_gliding", isGliding ? 1 : 0);
    }
}

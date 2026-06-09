package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Jetstream — Elytra (Rare, Max 3)
 * While gliding, periodically applies a forward velocity boost.
 */
public class JetstreamEnchant extends VortexEnchant {

    public JetstreamEnchant() {
        super("jetstream", "Jetstream", "elytra");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        double[] boost = {0.15, 0.22, 0.30};
        return "While gliding, periodically boosts your speed by §a+" + (int)(boost[level - 1] * 100) + "%§7.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        double[] boosts = {0.15, 0.22, 0.30};
        double boost = cfgd("boost", boosts[level - 1]);
        Vector vel = player.getVelocity();
        if (vel.length() > 0.1) {
            player.setVelocity(vel.multiply(1.0 + boost));
        }
    }
}

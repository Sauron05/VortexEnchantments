package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

/**
 * Featherweight — Elytra (Common, Max 3)
 * Reduces fall damage taken while wearing an elytra (soft-landing bonus).
 */
public class FeatherweightEnchant extends VortexEnchant {

    public FeatherweightEnchant() {
        super("featherweight", "Featherweight", "elytra");
    }

    @Override
    public String getTier() { return "COMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] red = {25, 50, 100};
        return "Reduces fall damage by §a" + red[level - 1] + "%§7 after gliding.";
    }

    @Override
    public void onDamageTaken(org.bukkit.event.entity.EntityDamageEvent event, Player player, int level) {
        // Note: fall damage is handled separately via EntityDamageEvent, not EntityDamageByEntity.
        // This hook is a fallback; main logic in the listener for FALL cause.
    }

    /**
     * Called by the listener when cause is FALL and player was recently gliding.
     */
    public double applyFallReduction(Player player, double damage, int level) {
        double[] reductions = {0.25, 0.50, 1.00};
        double reduction = cfgd("fall_reduction", reductions[level - 1]);
        return damage * (1.0 - reduction);
    }
}

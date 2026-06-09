package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * ThermalRider — Elytra (Uncommon, Max 3)
 * While gliding through high-altitude air (Y > 150/130/110), gain Speed I.
 */
public class ThermalRiderEnchant extends VortexEnchant {

    public ThermalRiderEnchant() {
        super("thermal_rider", "ThermalRider", "elytra");
    }

    @Override
    public String getTier() { return "UNCOMMON"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] thresh = {150, 130, 110};
        return "Gliding above Y=" + thresh[level - 1] + " grants §aSpeed I§7.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        int[] thresholds = {150, 130, 110};
        int thresh = cfgi("altitude_threshold", thresholds[level - 1]);
        if (player.getLocation().getY() >= thresh) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, true, false, false));
        }
    }
}

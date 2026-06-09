package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Updraft — Elytra (Rare, Max 3)
 * While below Y=64/50/40 and gliding, gain Levitation to rise back up.
 */
public class UpdraftEnchant extends VortexEnchant {

    public UpdraftEnchant() {
        super("updraft", "Updraft", "elytra");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] thresh = {64, 50, 40};
        return "While gliding below Y=" + thresh[level - 1] + ", gain §aLevitation§7 to rise.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        int[] thresholds = {64, 50, 40};
        int thresh = cfgi("min_altitude", thresholds[level - 1]);
        if (player.getLocation().getY() < thresh) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 0, true, false, false));
        }
    }
}

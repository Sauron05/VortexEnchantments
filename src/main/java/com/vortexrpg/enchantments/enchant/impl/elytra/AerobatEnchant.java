package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Aerobat — Elytra (Rare, Max 3)
 * While gliding and looking at the ground at a steep angle, gain Swiftness + Slow Falling.
 */
public class AerobatEnchant extends VortexEnchant {

    public AerobatEnchant() {
        super("aerobat", "Aerobat", "elytra");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "Performing steep gliding maneuvers grants §aSpeed §7and §aSlow Falling§7.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        double pitch = player.getLocation().getPitch(); // -90=up, 90=down
        double minPitch = cfgd("min_pitch", 30.0); // looking downward enough
        if (pitch > minPitch) {
            int amp = level - 1;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, amp, true, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 40, 0, true, false, false));
        }
    }
}

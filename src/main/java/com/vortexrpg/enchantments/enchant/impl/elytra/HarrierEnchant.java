package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Harrier — Elytra (Rare, Max 3)
 * While gliding and descending at moderate speed, gain Strength to attack from above.
 */
public class HarrierEnchant extends VortexEnchant {

    public HarrierEnchant() {
        super("harrier", "Harrier", "elytra");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "While gliding downward, gain §cStrength §7for aerial attacks.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        double vy = player.getVelocity().getY();
        if (vy < cfgd("min_descent", -0.3)) {
            int amp = level - 1;
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, amp, true, false, false));
        }
    }
}

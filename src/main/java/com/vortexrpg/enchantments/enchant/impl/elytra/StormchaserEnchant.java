package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Stormchaser — Elytra (Rare, Max 3)
 * While gliding during a thunderstorm, gain Haste I and Speed I.
 */
public class StormchaserEnchant extends VortexEnchant {

    public StormchaserEnchant() {
        super("stormchaser", "Stormchaser", "elytra");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        return "During a thunderstorm, gliding grants §aHaste I§7 and §aSpeed I§7.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isGliding()) return;
        if (!player.getWorld().isThundering()) return;
        int amp = level - 1;
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, amp, true, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, amp, true, false, false));
    }
}

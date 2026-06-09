package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Fortress — Shield (Epic, Max 3)
 * While continuously blocking for min_hold_ticks, gain Resistance I.
 */
public class FortressEnchant extends VortexEnchant {

    public FortressEnchant() {
        super("fortress", "Fortress", "shield");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int secs = 3 - (level - 1); // 2/1.5/1 shown simplified
        return "Continuous blocking for " + secs + "s grants §aResistance I§7.";
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!player.isBlocking()) return;

        int minTicks = cfgi("min_hold_ticks", new int[]{40, 30, 20}[level - 1]);
        int held = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "fortress_ticks", 0);
        held++;
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "fortress_ticks", held);

        if (held >= minTicks) {
            int amp = cfgi("resistance_amplifier", 0);
            int dur = 40; // refresh every 2s while blocking
            if (!player.hasPotionEffect(PotionEffectType.RESISTANCE)
                    || player.getPotionEffect(PotionEffectType.RESISTANCE).getDuration() < 30) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, dur, amp, true, false, true));
            }
        }
    }
}

package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Ward — Shield (Rare, Max 3)
 * Blocking grants 3/4/5s immunity to negative potion effects after each successful block.
 */
public class WardEnchant extends VortexEnchant {

    public WardEnchant() {
        super("ward", "Ward", "shield");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] durations = {3, 4, 5};
        return "Blocking a hit grants §a" + durations[level - 1] + "s§7 of immunity to negative potion effects.";
    }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!player.isBlocking()) return;
        double[] seconds = {3, 4, 5};
        plugin.getPlayerDataManager().setLong(player.getUniqueId(), "ward_expiry",
                System.currentTimeMillis() + (long)(cfgd("immunity_duration", seconds[level - 1]) * 1000));
    }

    @Override
    public void tickPassive(Player player, int level) {
        long expiry = plugin.getPlayerDataManager().getLong(player.getUniqueId(), "ward_expiry", 0L);
        if (System.currentTimeMillis() < expiry) {
            // Remove harmful effects while ward is active
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (isNegativeEffect(effect.getType())) {
                    player.removePotionEffect(effect.getType());
                }
            }
        }
    }

    private boolean isNegativeEffect(PotionEffectType type) {
        return type == PotionEffectType.POISON
                || type == PotionEffectType.WITHER
                || type == PotionEffectType.BLINDNESS
                || type == PotionEffectType.WEAKNESS
                || type == PotionEffectType.SLOWNESS
                || type == PotionEffectType.MINING_FATIGUE
                || type == PotionEffectType.NAUSEA
                || type == PotionEffectType.UNLUCK;
    }
}

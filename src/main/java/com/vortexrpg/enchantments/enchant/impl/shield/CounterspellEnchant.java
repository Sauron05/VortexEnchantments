package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 * Counterspell — Shield (Epic, Max 3)
 * While blocking, 15/20/25% chance to apply Mining Fatigue to attacker for 4s.
 */
public class CounterspellEnchant extends VortexEnchant {

    private static final Random RNG = new Random();

    public CounterspellEnchant() {
        super("counterspell", "Counterspell", "shield");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] chances = {15, 20, 25};
        return "Blocking has a §e" + chances[level - 1] + "%§7 chance to inflict §cMining Fatigue§7 on your attacker.";
    }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!player.isBlocking()) return;
        Entity attacker = event.getDamager();
        if (!(attacker instanceof LivingEntity le)) return;

        double[] chances = {0.15, 0.20, 0.25};
        if (RNG.nextDouble() < cfgd("chance", chances[level - 1])) {
            int dur = (int) (cfgd("duration", 4.0) * 20);
            le.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, dur, 1, false, true, true));
        }
    }
}

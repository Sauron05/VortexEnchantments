package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Riposte Shot: Shooting within 1/1.5/2s of being hit by melee = +80%/90%/100% arrow damage.
 */
public class RiposteShotEnchant extends VortexEnchant {

    private static final double[] WINDOW_SECS = {1.0, 1.5, 2.0};
    private static final double[] BONUS = {0.80, 0.90, 1.00};

    public RiposteShotEnchant() {
        super("riposte_shot", "Riposte Shot", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        long windowMs = (long)(cfg("window_seconds", WINDOW_SECS[level - 1]) * 1000);
        long lastMeleeHit = plugin.getPlayerDataManager().getLastMeleeDamageTime(shooter.getUniqueId());
        if (System.currentTimeMillis() - lastMeleeHit <= windowMs) {
            event.setDamage(event.getDamage() * (1.0 + cfg("bonus_damage_percent", BONUS[level - 1])));
            shooter.sendMessage("§6[Riposte Shot] §eRetribution!");
        }
    }

    @Override
    public String getDescription() { return "Shooting shortly after being hit melee deals massive bonus damage."; }

    @Override
    public String getDescription(int level) {
        return "§7Shoot within §e" + WINDOW_SECS[level-1] + "s§7 of melee hit: §a+" + (int)(BONUS[level-1]*100) + "%§7 arrow damage.";
    }
}

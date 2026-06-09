package com.vortexrpg.enchantments.enchant.impl.bow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Pendulum: Fully charged shots = -10%/-15%/-20% damage. Quick shots = +30%/+40%/+50%.
 */
public class PendulumEnchant extends VortexEnchant {

    private static final double[] CHARGED_PENALTY = {0.10, 0.15, 0.20};
    private static final double[] QUICK_BONUS = {0.30, 0.40, 0.50};

    public PendulumEnchant() {
        super("pendulum", "Pendulum", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof Arrow arrow)) return;

        if (arrow.isCritical()) {
            // Fully charged
            event.setDamage(event.getDamage() * (1.0 - cfg("charged_penalty", CHARGED_PENALTY[level - 1])));
        } else {
            // Quick/partial
            event.setDamage(event.getDamage() * (1.0 + cfg("quick_bonus", QUICK_BONUS[level - 1])));
        }
    }

    @Override
    public String getDescription() { return "Quick shots deal more damage; fully charged shots deal less."; }

    @Override
    public String getDescription(int level) {
        return "§7Quick shot: §a+" + (int)(QUICK_BONUS[level-1]*100) + "%§7. Full charge: §c-" + (int)(CHARGED_PENALTY[level-1]*100) + "%§7.";
    }
}

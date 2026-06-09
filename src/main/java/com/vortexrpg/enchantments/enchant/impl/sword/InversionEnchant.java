package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Inversion: Converts hunger bar value into damage bonus (+2%/3%/4% per hunger point).
 * Drains 1 hunger per swing.
 */
public class InversionEnchant extends VortexEnchant {

    private static final double[] BONUS_PER_HUNGER = {0.02, 0.03, 0.04};

    public InversionEnchant() {
        super("inversion", "Inversion", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double bonusPerHunger = cfg("bonus_per_hunger", BONUS_PER_HUNGER[level - 1]);
        int hungerDrain = cfgi("hunger_drain_per_swing", 1);

        int food = attacker.getFoodLevel();
        if (food > 0) {
            double bonus = food * bonusPerHunger;
            event.setDamage(event.getDamage() * (1.0 + bonus));
            int newFood = Math.max(0, food - hungerDrain);
            attacker.setFoodLevel(newFood);
        }
    }

    @Override
    public String getDescription() { return "Higher hunger = more damage dealt. Swinging drains hunger."; }

    @Override
    public String getDescription(int level) {
        return "§7+§e" + (int)(BONUS_PER_HUNGER[level - 1] * 100) + "%§7 damage per hunger point. Each swing drains §c1§7 hunger.";
    }
}

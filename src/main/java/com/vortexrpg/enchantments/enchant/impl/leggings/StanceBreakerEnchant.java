package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * StanceBreaker: Deals bonus damage to targets with active potion effects.
 */
public class StanceBreakerEnchant extends VortexEnchant {
    public StanceBreakerEnchant() { super("stance_breaker", "Stance Breaker", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (victim.getActivePotionEffects().isEmpty()) return;
        double bonusPer = cfgd("bonus_per_effect", 0.5);
        double bonus = bonusPer * Math.min(victim.getActivePotionEffects().size(), 5) * level;
        event.setDamage(event.getDamage() + bonus);
    }

    @Override public String getDescription(int level) {
        return "§7Bonus §c+0.5 §7damage per active potion effect on target (max 5).";
    }
}

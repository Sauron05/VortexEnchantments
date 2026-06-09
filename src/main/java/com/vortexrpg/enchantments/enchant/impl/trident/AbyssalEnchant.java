package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Abyssal: While submerged +30/35/40% damage, +15/20/25% throw speed. */
public class AbyssalEnchant extends VortexEnchant {
    private static final double[] DMG_BONUS = {0.30, 0.35, 0.40};

    public AbyssalEnchant() { super("abyssal", "Abyssal", EnchantRarity.EPIC, 3, List.of(ItemTarget.TRIDENT)); }

    private void apply(EntityDamageByEntityEvent event, Player attacker, int level) {
        if (!isEnabled()) return;
        if (!attacker.isUnderWater()) return;
        event.setDamage(event.getDamage() * (1.0 + DMG_BONUS[level-1]));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        apply(event, attacker, level);
    }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        apply(event, thrower, level);
    }

    @Override public String getDescription() { return "Stronger when submerged."; }
    @Override public String getDescription(int level) {
        return "§7Underwater: §c+" + (int)(DMG_BONUS[level-1]*100) + "%§7 damage."; }
}

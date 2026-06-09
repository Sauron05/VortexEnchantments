package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Leviathan: +40/50/60% damage vs mobs taller than 2 blocks. */
public class LeviathanEnchant extends VortexEnchant {
    private static final double[] BONUS = {0.40, 0.50, 0.60};

    public LeviathanEnchant() { super("leviathan", "Leviathan", EnchantRarity.EPIC, 3, List.of(ItemTarget.TRIDENT)); }

    private void apply(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        if (!isEnabled()) return;
        double sizeThreshold = cfg("size_threshold", 2.0);
        if (target.getHeight() < sizeThreshold) return;
        event.setDamage(event.getDamage() * (1.0 + BONUS[level-1]));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        apply(event, attacker, target, level);
    }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        apply(event, thrower, target, level);
    }

    @Override public String getDescription() { return "Deals extra damage to large mobs."; }
    @Override public String getDescription(int level) {
        return "§c+" + (int)(BONUS[level-1]*100) + "%§7 damage vs mobs taller than §e2 blocks§7."; }
}

package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Depth: +5/7/10% damage per block below sea level. */
public class DepthEnchant extends VortexEnchant {
    private static final double[] BONUS = {0.05, 0.07, 0.10};
    public DepthEnchant() { super("depth", "Depth", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        if (!isEnabled()) return;
        int seaLevel = cfgi("sea_level", 63);
        int y = attacker.getLocation().getBlockY();
        if (y >= seaLevel) return;
        int depth = seaLevel - y;
        double bonus = cfg("bonus_per_block", BONUS[level-1]) * depth;
        event.setDamage(event.getDamage() * (1.0 + bonus));
    }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        onAttack(event, thrower, target, level);
    }

    @Override public String getDescription() { return "Deals more damage the deeper you are."; }
    @Override public String getDescription(int level) {
        return "§7+" + (int)(BONUS[level-1]*100) + "%§7 damage per block below sea level."; }
}

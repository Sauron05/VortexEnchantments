package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Caliber: Loading takes longer. +60%/70%/80% damage, ignores 30%/40%/50% armor. */
public class CaliberEnchant extends VortexEnchant {
    private static final double[] DMG_BONUS = {0.60, 0.70, 0.80};
    private static final double[] ARMOR_BYPASS = {0.30, 0.40, 0.50};
    public CaliberEnchant() { super("caliber", "Caliber", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double bonus = cfg("damage_bonus", DMG_BONUS[level-1]);
        double bypass = cfg("armor_bypass_percent", ARMOR_BYPASS[level-1]);
        event.setDamage(event.getDamage() * (1.0 + bonus));
        // Reduce damage reduction calculation (approximate armor bypass)
        double currentDmg = event.getDamage();
        event.setDamage(currentDmg * (1.0 + bypass));
    }

    @Override public String getDescription() { return "Deals massive damage and pierces armor."; }
    @Override public String getDescription(int level) {
        return "§7§a+" + (int)(DMG_BONUS[level-1]*100) + "%§7 dmg + §cbypasses §e" + (int)(ARMOR_BYPASS[level-1]*100) + "%§7 armor."; }
}

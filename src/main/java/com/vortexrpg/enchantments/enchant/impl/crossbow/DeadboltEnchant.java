package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Deadbolt: Bolt pierces through first entity, hitting second for 40%/50%/60%. */
public class DeadboltEnchant extends VortexEnchant {
    private static final double[] PIERCE_DMG = {0.40, 0.50, 0.60};
    public DeadboltEnchant() { super("deadbolt", "Deadbolt", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double dmgPct = cfg("pierce_damage_percent", PIERCE_DMG[level-1]);
        LivingEntity behind = MathUtil.getNearestLiving(victim.getLocation(), 3.0,
            e -> !e.equals(victim) && !e.equals(shooter));
        if (behind != null) behind.damage(event.getDamage() * dmgPct, shooter);
    }

    @Override public String getDescription() { return "Bolts pierce through enemies, hitting those behind."; }
    @Override public String getDescription(int level) {
        return "§7Bolt pierces to §c" + (int)(PIERCE_DMG[level-1]*100) + "%§7 damage for entity behind target."; }
}

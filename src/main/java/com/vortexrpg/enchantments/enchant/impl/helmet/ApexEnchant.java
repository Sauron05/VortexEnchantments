package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Apex: Deal X% more damage to targets who have more max HP than you.
 */
public class ApexEnchant extends VortexEnchant {
    public ApexEnchant() { super("apex", "Apex", EnchantRarity.EPIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double myMaxHp = attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        var victimAttr = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
        if (victimAttr == null) return;
        double theirMaxHp = victimAttr.getValue();
        if (theirMaxHp > myMaxHp) {
            double bonus = cfgd("bonus_pct", 0.08 + level * 0.07);
            event.setDamage(event.getDamage() * (1.0 + bonus));
        }
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.08 + level * 0.07) * 100);
        return "§7Deal §a+" + pct + "%§7 damage to targets with more max HP.";
    }
}

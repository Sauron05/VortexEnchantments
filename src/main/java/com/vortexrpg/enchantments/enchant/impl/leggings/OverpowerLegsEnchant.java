package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * OverpowerLegs: deal bonus damage scaling with your current armor value.
 */
public class OverpowerLegsEnchant extends VortexEnchant {
    public OverpowerLegsEnchant() { super("overpower_legs", "Overpower Legs", EnchantRarity.EPIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double armor = attacker.getAttribute(Attribute.ARMOR).getValue();
        double bonusPer = cfgd("bonus_per_armor", 0.04 * level);
        double bonus = armor * bonusPer;
        event.setDamage(event.getDamage() + bonus);
    }

    @Override public String getDescription(int level) {
        return "§7Deal §c+" + (4 * level) + "% §7of your armor value as bonus damage.";
    }
}

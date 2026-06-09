package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Executioner Boots: Massive bonus damage to targets below 25% HP.
 */
public class ExecutionerBootsEnchant extends VortexEnchant {
    public ExecutionerBootsEnchant() { super("executioner_boots", "Executioner Boots", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double victimMaxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("hp_threshold", 0.25);
        if (victim.getHealth() / victimMaxHp > threshold) return;
        double bonus = cfgd("bonus_damage", 2.5 * level);
        event.setDamage(event.getDamage() + bonus);
    }

    @Override public String getDescription(int level) {
        return "§7Targets below 25% HP take §c+" + String.format("%.1f", 2.5 * level) + " §7bonus damage.";
    }
}

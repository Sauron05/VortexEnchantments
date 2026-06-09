package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Aggressor: Bonus damage when at full health.
 */
public class AggressorEnchant extends VortexEnchant {
    public AggressorEnchant() { super("aggressor", "Aggressor", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double maxHp = attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        if (attacker.getHealth() < maxHp) return;
        double bonus = cfgd("damage_bonus", 1.0 * level);
        event.setDamage(event.getDamage() + bonus);
    }

    @Override public String getDescription(int level) {
        return "§7At full HP: §c+" + level + " §7bonus melee damage.";
    }
}

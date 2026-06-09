package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Berserker Plate: Lower HP = higher attack damage bonus.
 */
public class BerserkerPlateEnchant extends VortexEnchant {
    public BerserkerPlateEnchant() { super("berserker_plate", "Berserker Plate", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double maxHp = attacker.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double hpRatio = attacker.getHealth() / maxHp;
        double maxBonus = cfgd("max_bonus", 2.0 * level);
        double bonus = maxBonus * (1.0 - hpRatio);
        event.setDamage(event.getDamage() + bonus);
    }

    @Override public String getDescription(int level) {
        return "§7Lower HP = more damage. Up to §c+" + (2 * level) + " §7bonus at 0% HP.";
    }
}

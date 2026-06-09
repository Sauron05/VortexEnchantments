package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * LifeSteal Boots: Steal HP on melee attacks.
 */
public class LifeStealBootsEnchant extends VortexEnchant {
    public LifeStealBootsEnchant() { super("life_steal_boots", "Life Steal Boots", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        double pct = cfgd("steal_pct", 0.04 * level);
        double steal = event.getDamage() * pct;
        double maxHp = attacker.getAttribute(Attribute.MAX_HEALTH).getValue();
        attacker.setHealth(Math.min(maxHp, attacker.getHealth() + steal));
    }

    @Override public String getDescription(int level) {
        return "§7Steal §c" + (4 * level) + "% §7of melee damage as HP.";
    }
}

package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * LastStand: While below 20% HP, incoming damage is capped per hit.
 */
public class LastStandEnchant extends VortexEnchant {
    public LastStandEnchant() { super("last_stand", "Last Stand", EnchantRarity.EPIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double maxHp = victim.getAttribute(Attribute.MAX_HEALTH).getValue();
        if (victim.getHealth() / maxHp > cfgd("hp_threshold", 0.20)) return;
        double cap = cfgd("damage_cap", 4.0 - level * 0.5);
        if (event.getDamage() > cap) {
            event.setDamage(cap);
        }
    }

    @Override public String getDescription(int level) {
        return "§7Below 20% HP: incoming melee damage capped at §c" + String.format("%.1f", 4.0 - level * 0.5) + "§7.";
    }
}

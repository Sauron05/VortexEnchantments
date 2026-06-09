package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * KiteTactics: Bonus damage to targets further away.
 */
public class KiteTacticsEnchant extends VortexEnchant {
    public KiteTacticsEnchant() { super("kite_tactics", "Kite Tactics", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double dist = victim.getLocation().distance(attacker.getLocation());
        double threshold = cfgd("distance_threshold", 3.0);
        if (dist < threshold) return;
        double reduction = cfgd("reduction_pct", 0.06 * level);
        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    @Override public String getDescription(int level) {
        return "§7Take §a" + (6 * level) + "% §7less damage from attackers 3+ blocks away.";
    }
}

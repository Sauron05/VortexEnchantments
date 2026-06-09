package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Armored Legs: Small flat damage reduction from melee hits.
 */
public class ArmoredLegsEnchant extends VortexEnchant {
    public ArmoredLegsEnchant() { super("armored_legs", "Armored Legs", EnchantRarity.COMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double reduction = cfgd("flat_reduction", 0.5 * level);
        event.setDamage(Math.max(0, event.getDamage() - reduction));
    }

    @Override public String getDescription(int level) {
        return "§7Reduces melee damage by §a" + String.format("%.1f", 0.5 * level) + "§7.";
    }
}

package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Stalwart: Reduces critical hit bonus damage.
 */
public class StalwartEnchant extends VortexEnchant {
    public StalwartEnchant() { super("stalwart", "Stalwart", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof Player atkPlayer)) return;
        // Critical hits occur when falling and not on ground
        if (atkPlayer.getFallDistance() <= 0) return;
        double reduction = cfgd("crit_reduction", 0.15 * level);
        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    @Override public String getDescription(int level) {
        return "§7Reduces critical hit bonus damage by §a" + (15 * level) + "%§7.";
    }
}

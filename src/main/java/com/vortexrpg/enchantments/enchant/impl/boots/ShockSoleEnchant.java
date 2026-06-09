package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * ShockSole: Reduces cactus and thorns damage.
 */
public class ShockSoleEnchant extends VortexEnchant {
    public ShockSoleEnchant() { super("shock_sole", "Shock Sole", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.CONTACT && cause != EntityDamageEvent.DamageCause.THORNS) return;
        double pct = cfgd("reduction_pct", 0.20 * level);
        event.setDamage(event.getDamage() * (1.0 - pct));
    }

    @Override public String getDescription(int level) {
        return "§7Reduces thorns/cactus damage by §a" + (20 * level) + "%§7.";
    }
}

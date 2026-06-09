package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * PaddedSoles: Reduces fall damage by a flat amount.
 */
public class PaddedSolesEnchant extends VortexEnchant {
    public PaddedSolesEnchant() { super("padded_soles", "Padded Soles", EnchantRarity.COMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        double reduction = cfgd("reduction", 2.0 * level);
        event.setDamage(Math.max(0, event.getDamage() - reduction));
    }

    @Override public String getDescription(int level) {
        return "§7Reduces fall damage by §a" + (2 * level) + "§7.";
    }
}

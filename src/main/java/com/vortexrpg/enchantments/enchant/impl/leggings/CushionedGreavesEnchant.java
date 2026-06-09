package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Cushioned Greaves: Reduces fall damage by a flat amount.
 */
public class CushionedGreavesEnchant extends VortexEnchant {
    public CushionedGreavesEnchant() { super("cushioned_greaves", "Cushioned Greaves", EnchantRarity.COMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        double reduction = cfgd("reduction", 1.0 + level * 0.5);
        event.setDamage(Math.max(0, event.getDamage() - reduction));
    }

    @Override public String getDescription(int level) {
        return "§7Reduces fall damage by §a" + String.format("%.1f", 1.0 + level * 0.5) + "§7.";
    }
}

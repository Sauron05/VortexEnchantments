package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Padded Vest: Reduces projectile damage by a flat amount per level.
 */
public class PaddedVestEnchant extends VortexEnchant {
    public PaddedVestEnchant() { super("padded_vest", "Padded Vest", EnchantRarity.COMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;
        double reduction = cfgd("reduction", 1.0 + level * 0.5);
        event.setDamage(Math.max(0, event.getDamage() - reduction));
    }

    @Override public String getDescription(int level) {
        return "§7Reduces projectile damage by §a" + String.format("%.1f", 1.0 + level * 0.5) + "§7.";
    }
}

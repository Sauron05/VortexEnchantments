package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Rune Shield: Reduces magic and potion damage.
 */
public class RuneShieldEnchant extends VortexEnchant {
    public RuneShieldEnchant() { super("rune_shield", "Rune Shield", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.MAGIC && cause != EntityDamageEvent.DamageCause.POISON
                && cause != EntityDamageEvent.DamageCause.WITHER && cause != EntityDamageEvent.DamageCause.DRAGON_BREATH) return;
        double pct = cfgd("reduction_pct", 0.10 * level);
        event.setDamage(event.getDamage() * (1.0 - pct));
    }

    @Override public String getDescription(int level) {
        return "§7Reduces magic/potion damage by §a" + (10 * level) + "%§7.";
    }
}

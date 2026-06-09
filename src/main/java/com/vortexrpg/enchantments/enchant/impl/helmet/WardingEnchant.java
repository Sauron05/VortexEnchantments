package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Warding: Reduces magic/AOE damage (potions, dragon breath) by X%.
 */
public class WardingEnchant extends VortexEnchant {
    public WardingEnchant() { super("warding", "Warding", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(org.bukkit.event.entity.EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        var cause = event.getCause();
        if (cause != org.bukkit.event.entity.EntityDamageEvent.DamageCause.MAGIC
                && cause != org.bukkit.event.entity.EntityDamageEvent.DamageCause.DRAGON_BREATH) return;
        double pct = cfgd("reduce_pct", 0.10 + level * 0.10);
        event.setDamage(event.getDamage() * (1.0 - pct));
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.10) * 100);
        return "§7Reduces magic damage by §a" + pct + "%§7.";
    }
}

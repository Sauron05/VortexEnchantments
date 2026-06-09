package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Layered: Small percentage damage reduction from all sources.
 */
public class LayeredEnchant extends VortexEnchant {
    public LayeredEnchant() { super("layered", "Layered", EnchantRarity.COMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double pct = cfgd("reduction_pct", 0.03 * level);
        event.setDamage(event.getDamage() * (1.0 - pct));
    }

    @Override public String getDescription(int level) {
        return "§7Reduces all damage by §a" + (int)(3 * level) + "%§7.";
    }
}

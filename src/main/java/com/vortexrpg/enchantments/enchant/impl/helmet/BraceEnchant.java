package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** Brace: Reduces fall damage by X%. */
public class BraceEnchant extends VortexEnchant {
    public BraceEnchant() { super("brace", "Brace", EnchantRarity.COMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        double pct = cfgd("reduce_pct", 0.10 + level * 0.10);
        event.setDamage(event.getDamage() * (1.0 - pct));
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.10) * 100);
        return "§7Reduces fall damage by §a" + pct + "%§7.";
    }
}

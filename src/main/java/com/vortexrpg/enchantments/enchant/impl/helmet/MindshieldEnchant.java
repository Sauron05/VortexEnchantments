package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Mindshield: Reduces Wither and Poison damage while wearing the helmet. */
public class MindshieldEnchant extends VortexEnchant {
    public MindshieldEnchant() { super("mindshield", "Mindshield", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(org.bukkit.event.entity.EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        var cause = event.getCause();
        if (cause != org.bukkit.event.entity.EntityDamageEvent.DamageCause.WITHER
                && cause != org.bukkit.event.entity.EntityDamageEvent.DamageCause.POISON) return;
        double pct = cfgd("reduce_pct", 0.15 + level * 0.15);
        event.setDamage(event.getDamage() * (1.0 - pct));
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.15 + level * 0.15) * 100);
        return "§7Reduces Wither/Poison damage by §a" + pct + "%§7.";
    }
}

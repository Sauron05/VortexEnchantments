package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Padded: Reduces headshot (projectile) damage by a flat amount. */
public class PaddedEnchant extends VortexEnchant {
    public PaddedEnchant() { super("padded", "Padded", EnchantRarity.COMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof org.bukkit.entity.Projectile)) return;
        double reduction = cfgd("flat_reduction", 0.5 + level * 0.5);
        event.setDamage(Math.max(0, event.getDamage() - reduction));
    }

    @Override public String getDescription(int level) {
        return "§7Projectile damage reduced by §a" + (0.5 + level * 0.5) + "§7 hearts.";
    }
}

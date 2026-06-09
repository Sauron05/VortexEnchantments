package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Stoneskin: Sneaking reduces incoming melee damage.
 */
public class StoneskinEnchant extends VortexEnchant {
    public StoneskinEnchant() { super("stoneskin", "Stoneskin", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!victim.isSneaking()) return;
        double pct = cfgd("sneak_reduction", 0.08 * level);
        event.setDamage(event.getDamage() * (1.0 - pct));
    }

    @Override public String getDescription(int level) {
        return "§7While sneaking: melee damage reduced by §a" + (8 * level) + "%§7.";
    }
}

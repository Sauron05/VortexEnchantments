package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * SpikedSoles: Reflect a portion of melee damage back to attacker.
 */
public class SpikedSolesEnchant extends VortexEnchant {
    public SpikedSolesEnchant() { super("spiked_soles", "Spiked Soles", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (event.getCause() != EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK) return;
        double pct = cfgd("reflect_pct", 0.08 * level);
        double reflect = event.getDamage() * pct;
        if (attacker instanceof org.bukkit.entity.LivingEntity living) {
            living.damage(reflect, victim);
        }
    }

    @Override public String getDescription(int level) {
        return "§7Reflect §c" + (8 * level) + "% §7of melee damage back.";
    }
}

package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Vendetta: Increased damage against the last entity that damaged you.
 */
public class VendettaEnchant extends VortexEnchant {
    private static final Map<UUID, UUID> LAST_ATTACKER = new HashMap<>();

    public VendettaEnchant() { super("vendetta", "Vendetta", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        LAST_ATTACKER.put(victim.getUniqueId(), attacker.getUniqueId());
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        UUID lastAtk = LAST_ATTACKER.get(attacker.getUniqueId());
        if (lastAtk == null || !lastAtk.equals(victim.getUniqueId())) return;
        double bonus = cfgd("revenge_bonus", 1.5 * level);
        event.setDamage(event.getDamage() + bonus);
    }

    @Override public String getDescription(int level) {
        return "§7Deal §c+" + String.format("%.1f", 1.5 * level) + " §7bonus damage to your last attacker.";
    }
}

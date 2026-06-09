package com.vortexrpg.enchantments.enchant.impl.chestplate;

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
 * Counterstrike: After taking damage, next melee attack deals bonus damage.
 */
public class CounterstrikeEnchant extends VortexEnchant {
    private static final Map<UUID, Long> HIT_TIME = new HashMap<>();

    public CounterstrikeEnchant() { super("counterstrike", "Counterstrike", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        HIT_TIME.put(victim.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        Long hitTime = HIT_TIME.remove(attacker.getUniqueId());
        if (hitTime == null) return;
        long window = cfgi("window_ms", 3000);
        if (System.currentTimeMillis() - hitTime > window) return;
        double bonus = cfgd("bonus_damage", 1.5 + level * 1.0);
        event.setDamage(event.getDamage() + bonus);
    }

    @Override public String getDescription(int level) {
        return "§7After being hit, next attack within 3s deals §c+" + String.format("%.1f", 1.5 + level) + " §7bonus damage.";
    }
}

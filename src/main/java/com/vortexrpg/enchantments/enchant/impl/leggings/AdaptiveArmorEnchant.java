package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AdaptiveArmor: Each consecutive hit from the same source deals less damage (stacks reset per source).
 */
public class AdaptiveArmorEnchant extends VortexEnchant {
    private static final Map<UUID, Map<UUID, Integer>> STACKS = new HashMap<>();

    public AdaptiveArmorEnchant() { super("adaptive_armor", "Adaptive Armor", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        Map<UUID, Integer> victimStacks = STACKS.computeIfAbsent(victim.getUniqueId(), k -> new HashMap<>());
        int stacks = victimStacks.getOrDefault(attacker.getUniqueId(), 0);
        double pctPer = cfgd("reduction_per_stack", 0.06 * level);
        int maxStacks = cfgi("max_stacks", 5);
        stacks = Math.min(stacks, maxStacks);
        double reduction = stacks * pctPer;
        event.setDamage(event.getDamage() * (1.0 - Math.min(reduction, 0.8)));
        victimStacks.put(attacker.getUniqueId(), stacks + 1);

        // Reset stacks for other attackers
        for (UUID key : List.copyOf(victimStacks.keySet())) {
            if (!key.equals(attacker.getUniqueId())) {
                victimStacks.remove(key);
            }
        }
    }

    @Override public String getDescription(int level) {
        return "§7Each hit from same source deals §a" + (6 * level) + "% §7less (stacks 5x).";
    }
}

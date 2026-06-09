package com.vortexrpg.enchantments.enchant.impl.boots;

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
 * TitanAnkle: Each consecutive hit from the same attacker is reduced.
 */
public class TitanAnkleEnchant extends VortexEnchant {
    private static final Map<UUID, Map<UUID, Integer>> STACKS = new HashMap<>();

    public TitanAnkleEnchant() { super("titan_ankle", "Titan Ankle", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        Map<UUID, Integer> stacks = STACKS.computeIfAbsent(victim.getUniqueId(), k -> new HashMap<>());
        int count = stacks.getOrDefault(attacker.getUniqueId(), 0);
        int maxStacks = cfgi("max_stacks", 4);
        count = Math.min(count, maxStacks);
        double reductionPer = cfgd("reduction_per_stack", 0.04 * level);
        event.setDamage(event.getDamage() * (1.0 - Math.min(count * reductionPer, 0.6)));
        stacks.put(attacker.getUniqueId(), count + 1);
    }

    @Override public String getDescription(int level) {
        return "§7Consecutive hits from same source deal §a" + (4 * level) + "% §7less (stacks 4x).";
    }
}

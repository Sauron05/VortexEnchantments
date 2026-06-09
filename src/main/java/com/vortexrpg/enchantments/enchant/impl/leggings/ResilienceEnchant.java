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
 * Resilience: Consecutive hits within a window deal less damage (stacking defense).
 */
public class ResilienceEnchant extends VortexEnchant {
    private static final Map<UUID, long[]> HITS = new HashMap<>();

    public ResilienceEnchant() { super("resilience", "Resilience", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        long window = cfgi("window_ms", 5000);
        long[] data = HITS.getOrDefault(victim.getUniqueId(), new long[]{0, 0});
        if (now - data[1] > window) data[0] = 0;
        data[0]++;
        data[1] = now;
        HITS.put(victim.getUniqueId(), data);
        int maxStacks = cfgi("max_stacks", 3 + level);
        int stacks = (int) Math.min(data[0], maxStacks);
        double pctPer = cfgd("reduction_per_stack", 0.04);
        event.setDamage(event.getDamage() * (1.0 - stacks * pctPer));
    }

    @Override public String getDescription(int level) {
        return "§7Consecutive hits within 5s deal §a4% §7less each, up to §a" + (3 + level) + " §7stacks.";
    }
}
